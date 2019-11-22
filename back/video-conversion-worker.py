#!/usr/bin/python3.5

import logging

from configuration import Configuration
from pubsubmessaging import PubSubMessaging
from videoconversion import VideoConversion
from videoconversionunixsocket import VideoConversionUnixSocket

if __name__ == '__main__':

    logging.basicConfig(format='%(asctime)s - %(levelname)s: %(message)s', level=logging.DEBUG)
    configuration = Configuration()
    logging.info(configuration.get_database_name())
    logging.info(configuration.get_video_conversion_collection())
    video_unix_socket = VideoConversionUnixSocket()
    video_unix_socket.start()
    video_conversion_service = VideoConversion(configuration)
    video_messaging = PubSubMessaging(configuration, video_conversion_service)
    video_unix_socket.setVideoConversionMessaging(video_messaging)

