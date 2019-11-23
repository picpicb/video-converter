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
        # Split de l'URI : bucket/file
        bucketURI = _uri_.split('/')[0]
        fileName = _uri_.split('/')[1]
        converted = fileName.replace(".mkv", "-converted.avi")

        # Recuperation du fichier depuis Google Storage
        bucket = self.storage_client.get_bucket(bucketURI)
        blob = bucket.blob(fileName)
        if not os.path.exists(self.tmp_directory):
            os.makedirs(self.tmp_directory)
        blob.download_to_filename(self.tmp_directory+fileName)

        # Encodage ffmpeg
        ff = ffmpy.FFmpeg(
            inputs={self.tmp_directory+fileName: None},
            outputs={self.tmp_directory+converted: '-y -vcodec mpeg4 -b 4000k -acodec mp2 -ab 320k'}
        )
        logging.info("FFMPEG = %s", ff.cmd)
        ff.run()

        # Depot du fichier converti sur Google Storage
        blobconversion = bucket.blob(converted)
        blobconversion.upload_from_filename(self.tmp_directory+converted)

        # Mise a jour du document dans la BD
        self.video_conversion_collection.update({'_id' : _id_}, { '$set' : {'originPath' : converted}})
        self.video_conversion_collection.update({'_id' : _id_}, { '$set' : {'tstamp' : time.time()  }})

        # Notification websocket
        payload = dict()
        payload["id"] = _id_;
        payload["status"] = 0;
        json_payload = json.dumps(payload)
        ws = websocket.create_connection(self.url, sslopt={"cert_reqs": ssl.CERT_NONE, "ca_certs" : "ca.cert.pem"})
        ws.send(json_payload);
        ws.close()

