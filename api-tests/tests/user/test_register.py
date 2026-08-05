import uuid
import pytest


# 201
def test_register_success(user_api):
    response = user_api.register(
        f"{uuid.uuid4().hex}@test.com",
        "password123",
    )

    assert response.status_code == 201


# 400
@pytest.mark.parametrize(
    "email,password",
    [
        ("", "password123"),
        ("invalid-email", "password123"),
        ("test@test.com", ""),
    ],
)
def test_register_validation(user_api, email, password):
    response = user_api.register(email, password)

    assert response.status_code == 400


# 409
def test_register_duplicate(user_api):
    email = f"{uuid.uuid4().hex}@test.com"

    response = user_api.register(email, "password123")
    assert response.status_code == 201

    response = user_api.register(email, "password123")

    assert response.status_code == 409