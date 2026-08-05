import os

import requests
from dotenv import load_dotenv

load_dotenv()

MESSAGE_URL = os.getenv("MESSAGE_URL")


class ApiMessageClient:

    def __init__(self, session: requests.Session):
        self.session = session

    def get_messages(self):
        return self.session.get(
            MESSAGE_URL,
            timeout=10,
        )