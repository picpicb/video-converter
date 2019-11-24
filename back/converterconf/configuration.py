import logging
from yaml import load

logging.basicConfig(format='%(asctime)s - %(levelname)s: %(message)s', level=logging.DEBUG)


class Configuration(object):
    def __init__(self):
        self.configuration_file = "application.yml"
        self.configuration_data = None
        f = open(self.configuration_file, 'r')
        self.configuration_data = load(f.read())
        f.close()

    def get_pubsub_project(self):
        return self.configuration_data['conversion']['messaging']['pubsub']['project-id']
    def get_pubsub_subscription(self):
        return self.configuration_data['conversion']['messaging']['pubsub']['subscription-name']

    def get_database_host(self):
        return self.configuration_data['spring']['data']['mongodb']['host']
    def get_database_name(self):
        return self.configuration_data['spring']['data']['mongodb']['database']

    def get_video_conversion_collection(self):
        return self.configuration_data['spring']['data']['mongodb']['collections']['video-conversions']
    def get_video_status_callback_url(self):
        return self.configuration_data['conversion']['messaging']['video-status']['url']
