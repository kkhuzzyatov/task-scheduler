import { useState } from "react";
import styles from "./TaskForm.module.css";

interface Props {
  onSubmit: (
    data: {
      title: string;
      description: string;
    }
  ) => void;

  onCancel: () => void;
}

export default function TaskForm({
  onSubmit,
  onCancel,
}: Props) {
  const [title, setTitle] =
    useState("");

  const [description, setDescription] =
    useState("");


  function handleSubmit(
    event: React.FormEvent
  ) {
    event.preventDefault();

    if (!title.trim()) {
      return;
    }

    onSubmit({
      title: title.trim(),
      description: description.trim(),
    });

    setTitle("");
    setDescription("");
  }


  return (
    <form
      className={styles.form}
      onSubmit={handleSubmit}
    >

      <div className={styles.header}>
        <h2>
          Create task
        </h2>
      </div>


      <div className={styles.field}>
        <label htmlFor="title">
          Title
        </label>

        <input
          id="title"
          type="text"
          value={title}
          placeholder="Enter task title"
          maxLength={255}
          onChange={(event) =>
            setTitle(
              event.target.value
            )
          }
          required
        />
      </div>


      <div className={styles.field}>
        <label htmlFor="description">
          Description
        </label>

        <textarea
          id="description"
          value={description}
          placeholder="Add details about this task"
          maxLength={5000}
          onChange={(event) =>
            setDescription(
              event.target.value
            )
          }
        />
      </div>


      <div className={styles.actions}>

        <button
          type="button"
          className={styles.cancelButton}
          onClick={onCancel}
        >
          Cancel
        </button>


        <button
          type="submit"
          className={styles.createButton}
        >
          Create task
        </button>

      </div>

    </form>
  );
}