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

    task_id = response.json()["id"]

    response = authenticated_task_api.update_task(
        task_id,
        {
            "completed": True,
        },
    )

    assert response.status_code == 200

    body = response.json()

    assert body["completed"] is True
    assert body["completedAt"] is not None



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



# 401
def test_get_tasks_unauthorized(task_api):

    response = task_api.get_tasks()

    assert response.status_code == 401