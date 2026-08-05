import uuid


# 200
def test_me_success(auth_api, user_api):
    email = f"{uuid.uuid4().hex}@test.com"
    password = "password123"

    response = user_api.register(email, password)
    assert response.status_code == 201

    response = auth_api.login(email, password)
    assert response.status_code == 200

    token = response.json()["token"]

    user_api.session.headers.update({
        "Authorization": f"Bearer {token}"
    })

    response = user_api.me()

    assert response.status_code == 200

    body = response.json()

    assert body["email"] == email
    assert "id" in body


# 401
def test_me_unauthorized(user_api):
    response = user_api.me()

    assert response.status_code == 401