import os

import requests
from dotenv import load_dotenv

load_dotenv()

USER_URL = os.getenv("USER_URL")


class ApiUserClient:

    def __init__(self, session: requests.Session):
        self.session = session

    def register(self, email: str, password: str):
        return self.session.post(
            USER_URL,
            json={
                "email": email,
                "password": password,
            },
            timeout=10,
        )

    def me(self):
        return self.session.get(
            USER_URL,
            timeout=10,
        )