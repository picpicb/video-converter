import time

from threading import Thread

from google.api_core.exceptions import DeadlineExceeded
from google.cloud import pubsub_v1

class PubSubMessaging(Thread):

    def __init__(self, _config_, converting_service):
        Thread.__init__(self)
        self.project_id = _config_.get_pubsub_project()
        print(self.project_id)
        self.subscription_name = _config_.get_pubsub_subscription()
        print(self.subscription_name)
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path(self.project_id, self.subscription_name)
        # Limit the subscriber to only have ten outstanding messages at a time.
        self.flow_control = pubsub_v1.types.FlowControl(max_messages=1)
        self.start()


    def run(self):
        while True:
            try:
                self.response = self.subscriber.pull(self.subscription_path, max_messages=1)
                for msg in self.response.received_messages:
                    print("Received message:", msg.message.data)
                ack_ids = [msg.ack_id for msg in self.response.received_messages]
                self.subscriber.acknowledge(self.subscription_path, ack_ids)
            except DeadlineExceeded :
                print("No incoming task")
            time.sleep(5)
