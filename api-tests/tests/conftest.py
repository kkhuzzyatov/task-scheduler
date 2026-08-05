import uuid

import pytest
import requests

from auth_client import ApiAuthClient
from user_client import ApiUserClient


@pytest.fixture
def session():
    return requests.Session()


@pytest.fixture
def auth_api(session):
    return ApiAuthClient(session)


@pytest.fixture
def user_api(session):
    return ApiUserClient(session)


@pytest.fixture
def create_authenticated_user():
    def factory():
        session = requests.Session()

        auth = ApiAuthClient(session)
        user = ApiUserClient(session)

        email = f"user_{uuid.uuid4().hex}@test.com"
        password = "password123"

        # Register
        response = user.register(
            email,
            password,
        )

        assert response.status_code == 201

        # Login
        response = auth.login(
            email,
            password,
        )

        assert response.status_code == 200

        token = response.json()["token"]

        session.headers.update({
            "Authorization": f"Bearer {token}"
        })

        return auth, user

    return factory