import { useState } from "react";
import styles from "./TaskCard.module.css";
import type { Task } from "../api/taskApi";

interface Props {
  task: Task;

  onToggleComplete: (
    task: Task
  ) => void;

  onUpdate: (
    id: string,
    data: {
      title: string;
      description: string;
      completed: boolean;
    }
  ) => void;

  onDelete: (
    id: string
  ) => void;

  onDragStart: (
    id: string
  ) => void;

  onDrop: (
    id: string
  ) => void;
}

export default function TaskCard({
  task,
  onToggleComplete,
  onUpdate,
  onDelete,
  onDragStart,
  onDrop,
}: Props) {
  const [editing, setEditing] =
    useState(false);

  const [confirmDelete, setConfirmDelete] =
    useState(false);

  const [description, setDescription] =
    useState(task.description);


  const completed =
    task.completedAt !== null;


  function handleConfirmEdit() {
    onUpdate(
      task.id,
      {
        title: task.title,
        description,
        completed,
      }
    );

    setEditing(false);
  }


  function handleDelete() {
    onDelete(task.id);
    setConfirmDelete(false);
  }


  return (
    <div
      className={`${styles.card} ${
        completed
          ? styles.completed
          : ""
      }`}
      draggable
      onDragStart={() =>
        onDragStart(task.id)
      }
      onDragOver={(event) =>
        event.preventDefault()
      }
      onDrop={() =>
        onDrop(task.id)
      }
    >

      <div className={styles.content}>

        <button
          type="button"
          className={`${styles.check} ${
            completed
              ? styles.checked
              : ""
          }`}
          onClick={() =>
            onToggleComplete(task)
          }
        >
          {completed && "✓"}
        </button>


        <div className={styles.text}>

          <h3>
            {task.title}
          </h3>


          {editing ? (
            <textarea
              className={
                styles.descriptionInput
              }
              value={description}
              onChange={(event) =>
                setDescription(
                  event.target.value
                )
              }
            />
          ) : (
            <p>
              {task.description}
            </p>
          )}

        </div>

      </div>


      {!completed && (
        <div className={styles.actions}>

          {editing ? (
            <button
              className={
                styles.confirmButton
              }
              onClick={
                handleConfirmEdit
              }
            >
              Confirm
            </button>
          ) : (
            <button
              className={
                styles.editButton
              }
              onClick={() =>
                setEditing(true)
              }
            >
              Edit
            </button>
          )}


          {confirmDelete ? (
            <>
              <button
                className={
                  styles.cancelDeleteButton
                }
                onClick={() =>
                  setConfirmDelete(false)
                }
              >
                Cancel
              </button>

              <button
                className={
                  styles.deleteButton
                }
                onClick={handleDelete}
              >
                Confirm delete
              </button>
            </>
          ) : (
            <button
              className={
                styles.deleteButton
              }
              onClick={() =>
                setConfirmDelete(true)
              }
            >
              Delete
            </button>
          )}

        </div>
      )}

    </div>
  );
}