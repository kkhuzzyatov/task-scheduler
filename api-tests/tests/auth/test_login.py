import uuid
import pytest


# 200
def test_login_success(auth_api, user_api):
    email = f"{uuid.uuid4().hex}@test.com"
    password = "password123"

    response = user_api.register(email, password)
    assert response.status_code == 201

    response = auth_api.login(email, password)

    assert response.status_code == 200

    body = response.json()

    assert "token" in body
    assert isinstance(body["token"], str)
    assert body["token"]


# 400
@pytest.mark.parametrize(
    "email,password",
    [
        ("", "password123"),
        ("invalid-email", "password123"),
        ("test@test.com", ""),
    ],
)
def test_login_validation(auth_api, email, password):
    response = auth_api.login(email, password)

    assert response.status_code == 400


# 400
def test_login_wrong_password(auth_api, user_api):
    email = f"{uuid.uuid4().hex}@test.com"

    response = user_api.register(email, "password123")
    assert response.status_code == 201

    response = auth_api.login(email, "wrongpassword")

    assert response.status_code == 400


# 400
def test_login_unknown_user(auth_api):
    response = auth_api.login(
        f"{uuid.uuid4().hex}@test.com",
        "password123",
    )

    assert response.status_code == 400