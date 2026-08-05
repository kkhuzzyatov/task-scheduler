import os

import requests
from dotenv import load_dotenv

load_dotenv()

AUTH_URL = os.getenv("AUTH_URL")


class ApiAuthClient:

    def __init__(self, session: requests.Session):
        self.session = session

    def login(self, email: str, password: str):
        return self.session.post(
            f"{AUTH_URL}/login",
            json={
                "email": email,
                "password": password,
            },
            timeout=10,
        )