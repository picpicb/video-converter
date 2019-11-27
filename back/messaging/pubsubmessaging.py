import time
import json
import logging
from threading import Thread
from google.api_core.exceptions import DeadlineExceeded
from google.cloud import pubsub_v1

class PubSubMessaging(Thread):

    def __init__(self, _config_, converting_service):
        Thread.__init__(self)
        self.project_id = _config_.get_pubsub_project()
        self.subscription_name = _config_.get_pubsub_subscription()
        self.converting_service = converting_service
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path(self.project_id, self.subscription_name)
        self.flow_control = pubsub_v1.types.FlowControl(max_messages=1)
        self.start()


    def run(self):
        while True:
            try:
                self.response = self.subscriber.pull(self.subscription_path, max_messages=1)
                ack_ids = [msg.ack_id for msg in self.response.received_messages]
                self.subscriber.acknowledge(self.subscription_path, ack_ids)
                for msg in self.response.received_messages:
                    task = json.loads(msg.message.data)
                    self.on_message(task)
            except DeadlineExceeded :
                print("No incoming task")
            time.sleep(5)

    def on_message(self,task):
        logging.info('id = %s, URI = %s, FORMAT = %s', task["id"], task['originPath'],task["format"])
        self.converting_service.convert(task["id"], task['originPath'])
