import uuid


# 200
def test_get_tasks_empty(authenticated_task_api):

    response = authenticated_task_api.get_tasks()

    assert response.status_code == 200

    body = response.json()

    assert isinstance(body, list)
    assert len(body) == 0



# 201
def test_create_task(authenticated_task_api):

    response = authenticated_task_api.create_task(
        "First task",
        "Task description",
    )

    assert response.status_code == 201

    body = response.json()

    assert "id" in body
    assert body["title"] == "First task"
    assert body["description"] == "Task description"



# 200
def test_get_tasks_after_create(authenticated_task_api):

    authenticated_task_api.create_task(
        "My task",
        "Description",
    )

    response = authenticated_task_api.get_tasks()

    assert response.status_code == 200

    tasks = response.json()

    assert len(tasks) == 1
    assert tasks[0]["title"] == "My task"



# 200
def test_update_task(authenticated_task_api):

    response = authenticated_task_api.create_task(
        "Old title",
        "Old description",
    )

    assert response.status_code == 201

    task_id = response.json()["id"]

    response = authenticated_task_api.update_task(
        task_id,
        {
            "title": "New title",
            "description": "New description",
        },
    )

    assert response.status_code == 200

    body = response.json()

    assert body["title"] == "New title"
    assert body["description"] == "New description"



# 200
def test_complete_task(authenticated_task_api):

    response = authenticated_task_api.create_task(
        "Complete me",
    )

    assert response.status_code == 201

    task_id = response.json()["id"]

    response = authenticated_task_api.complete_task(task_id)

    assert response.status_code == 200

    body = response.json()

    assert body["completedAt"] is not None


# 200
def test_update_description(authenticated_task_api):
    response = authenticated_task_api.create_task(
        "Original title",
        "Original description",
    )

    assert response.status_code == 201

    task_id = response.json()["id"]

    response = authenticated_task_api.update_task(
        task_id,
        {
            "description": "Updated description",
        },
    )

    assert response.status_code == 200

    body = response.json()

    assert body["title"] == "Original title"
    assert body["description"] == "Updated description"



# 204
def test_delete_task(authenticated_task_api):

    response = authenticated_task_api.create_task(
        "Delete me",
    )

    task_id = response.json()["id"]

    response = authenticated_task_api.delete_task(task_id)

    assert response.status_code == 204


    response = authenticated_task_api.get_tasks()

    tasks = response.json()

    assert len(tasks) == 0


# 400
def test_create_task_with_empty_title(authenticated_task_api):

    response = authenticated_task_api.create_task(
        "",
        "Description",
    )

    assert response.status_code == 400



# 401
def test_get_tasks_unauthorized(task_api):

    response = task_api.get_tasks()

    assert response.status_code == 401

# 404
def test_update_other_users_task_forbidden(create_authenticated_user):
    _, user1 = create_authenticated_user()
    _, user2 = create_authenticated_user()

    from task_client import ApiTaskClient

    task_api_user1 = ApiTaskClient(user1.session)
    task_api_user2 = ApiTaskClient(user2.session)

    response = task_api_user1.create_task(
        "User 1 task",
        "Private task",
    )

    assert response.status_code == 201

    task_id = response.json()["id"]

    response = task_api_user2.update_task(
        task_id,
        {
            "title": "Hacked title",
        },
    )

    assert response.status_code == 404


# 404
def test_delete_other_users_task_forbidden(create_authenticated_user):
    _, user1 = create_authenticated_user()
    _, user2 = create_authenticated_user()

    from task_client import ApiTaskClient

    task_api_user1 = ApiTaskClient(user1.session)
    task_api_user2 = ApiTaskClient(user2.session)

    response = task_api_user1.create_task(
        "User 1 task",
    )

    assert response.status_code == 201

    task_id = response.json()["id"]

    response = task_api_user2.delete_task(task_id)

    assert response.status_code == 404