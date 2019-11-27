# coding=utf-8
import logging
import ffmpy
from google.cloud import storage
from pymongo import MongoClient
import time
import os
import websocket
import json
import ssl
import pexpect
import re

logging.basicConfig(format='%(asctime)s - %(levelname)s: %(message)s', level=logging.DEBUG)


class VideoConversion(object):
    def __init__(self, _config_):
        self.client = MongoClient(_config_.get_database_host())
        self.db = self.client[_config_.get_database_name()]
        self.video_conversion_collection = self.db[_config_.get_video_conversion_collection()]
        self.url = _config_.get_video_status_callback_url()
        self.storage_client = storage.Client()
        self.tmp_directory = "tmp/"

    def convert(self, _id_, _uri_):
        line = ""
        i = 0
        duration_total = 0
        self.ws = websocket.create_connection(self.url, sslopt={"cert_reqs": ssl.CERT_NONE, "ca_certs" : "ca.cert.pem"})

        # Split de l'URI : bucket/file
        bucketURI = _uri_.split('/')[0]
        fileName =  _uri_.split('/')[1]
        converted = fileName.replace(".mkv", "-converted.avi")

        # Recuperation du fichier depuis Google Storage
        bucket = self.storage_client.get_bucket(bucketURI)
        blob = bucket.blob(fileName)
        if not os.path.exists(self.tmp_directory):
            os.makedirs(self.tmp_directory)
        blob.download_to_filename(self.tmp_directory+fileName)

        # Creation de la commande ffmpeg
        ff = ffmpy.FFmpeg(
            inputs={self.tmp_directory+fileName: None},
            outputs={self.tmp_directory+converted: '-y -vcodec mpeg4 -b 4000k -acodec mp2 -ab 320k'}
        )
        logging.info("FFMPEG = %s", ff.cmd)
        cmd = ff.cmd
        thread = pexpect.spawn(cmd)


        while (not re.compile('^Press').match(line)):
            i = i + 1
            line = thread.readline().strip().decode('utf-8')
            if (re.compile('^Duration').match(line)):
                duration_total = self.timecode_value(line.split(',')[0].split(' ')[1])

        cpl = thread.compile_pattern_list([
            pexpect.EOF,
            "^(frame=.*)",
            '(.+)'
        ])


        while True:
            i = thread.expect_list(cpl, timeout=None)
            if i == 0:  # EOF
                self.send_ws_progress(100,_id_)
                print("the sub process exited")
                break
            elif i == 1:
                try:
                    array = tuple(re.sub(r"=\s+", '=', thread.match.group(0).decode('utf-8').strip()).split(' '))
                    time = array[4]
                    tc, ts = tuple(time.split('='))
                    current_time = self.timecode_value(tc=ts)
                    percentage = (current_time / duration_total * 100)
                    print("Avancement : %.2f" % percentage)
                    self.send_ws_progress(int(percentage),_id_)
                except:
                    print("exception")
                    self.send_ws_progress(666,_id_)
                thread.close
            elif i == 2:
                pass



        # Depot du fichier converti sur Google Storage
        blobconversion = bucket.blob(converted)
        blobconversion.upload_from_filename(self.tmp_directory+converted)

        # Mise a jour du document dans la BD
        self.video_conversion_collection.update({'_id' : _id_}, { '$set' : {'originPath' : converted}})
        if os.path.exists(self.tmp_directory+converted):
            os.remove(self.tmp_directory+converted)
        if os.path.exists(self.tmp_directory+fileName):
            os.remove(self.tmp_directory+fileName)
        self.ws.close()

        # Convert timecode in seconds
    def timecode_value(self, tc):
        print("timecode_value :" + tc)
        hours, minutes, seconds = tc.split(':')
        return float(seconds) + (float(minutes) * 60) + (float(hours) * 60 * 60)


        # Notification websocket
    def send_ws_progress(self, progress, id):
        payload = dict()
        payload["id"] = id;
        payload["progress"] = progress;
        json_payload = json.dumps(payload)
        self.ws.send(json_payload);
