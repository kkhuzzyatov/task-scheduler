import pytest
import uuid
import time


# 201
def test_register_success(user_api, message_api):
    email = f"{uuid.uuid4().hex}@test.com"

    response = user_api.register(
        email,
        "password123",
    )

    assert response.status_code == 201

    # Wait for Kafka consumer to process the message
    time.sleep(0.5)

    response = message_api.get_messages()

    assert response.status_code == 200

    body = response.json()

    assert "messages" in body

    assert (
        f"{email}: Welcome! - Спасибо за регистрацию!"
        in body["messages"]
    )


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