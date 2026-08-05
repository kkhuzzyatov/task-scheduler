import os

import requests
from dotenv import load_dotenv

load_dotenv()

TASK_URL = os.getenv("TASK_URL")


class ApiTaskClient:

    def __init__(self, session: requests.Session):
        self.session = session

    def get_tasks(self):
        return self.session.get(
            TASK_URL,
            timeout=10,
        )

    def create_task(self, title: str, description: str = None):
        return self.session.post(
            TASK_URL,
            json={
                "title": title,
                "description": description,
            },
            timeout=10,
        )

    def update_task(self, task_id: str, data: dict):
        return self.session.patch(
            f"{TASK_URL}/{task_id}",
            json=data,
            timeout=10,
        )

    def delete_task(self, task_id: str):
        return self.session.delete(
            f"{TASK_URL}/{task_id}",
            timeout=10,
        )