import { useEffect, useState } from "react";

import Header from "../../components/Header";
import TaskCard from "../../components/TaskCard";
import TaskForm from "../../components/TaskForm";

import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
} from "../../api/taskApi";

import type {
  Task,
} from "../../api/taskApi";

import styles from "./TasksPage.module.css";


type Filter =
  | "all"
  | "active"
  | "completed";


export default function TasksPage() {

  const [tasks, setTasks] =
    useState<Task[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [showForm, setShowForm] =
    useState(false);

  const [filter, setFilter] =
    useState<Filter>("all");

  const [search, setSearch] =
    useState("");

  const [draggedTaskId, setDraggedTaskId] =
    useState<string | null>(null);



  useEffect(() => {
    loadTasks();
  }, []);



  async function loadTasks() {
    try {
      const data =
        await getTasks();

      setTasks(data);

    } catch {
      setError(
        "Failed to load tasks"
      );

    } finally {
      setLoading(false);
    }
  }



  async function handleCreateTask(
    data: {
      title: string;
      description: string;
    }
  ) {
    const task =
      await createTask(data);

    setTasks((prev) => [
      ...prev,
      task,
    ]);

    setShowForm(false);
  }



  async function handleToggleComplete(
    task: Task
  ) {

    const updated =
      await updateTask(
        task.id,
        {
          title: task.title,
          description: task.description,
          completed:
            task.completedAt === null,
        }
      );


    setTasks((prev) =>
      prev.map((item) =>
        item.id === updated.id
          ? updated
          : item
      )
    );
  }



  async function handleUpdateTask(
    id: string,
    data: {
      title: string;
      description: string;
      completed: boolean;
    }
  ) {

    const updated =
      await updateTask(
        id,
        data
      );


    setTasks((prev) =>
      prev.map((item) =>
        item.id === updated.id
          ? updated
          : item
      )
    );
  }



  async function handleDeleteTask(
    id: string
  ) {

    await deleteTask(id);


    setTasks((prev) =>
      prev.filter(
        (task) =>
          task.id !== id
      )
    );
  }



  function handleDragStart(
    id: string
  ) {
    setDraggedTaskId(id);
  }



  function handleDrop(
    targetId: string
  ) {

    if (
      !draggedTaskId ||
      draggedTaskId === targetId
    ) {
      return;
    }


    setTasks((prev) => {

      const result = [
        ...prev,
      ];


      const from =
        result.findIndex(
          (task) =>
            task.id === draggedTaskId
        );


      const to =
        result.findIndex(
          (task) =>
            task.id === targetId
        );


      const [moved] =
        result.splice(
          from,
          1
        );


      result.splice(
        to,
        0,
        moved
      );


      return result;
    });


    setDraggedTaskId(null);
  }



  const visibleTasks =
    tasks.filter((task) => {

      const matchesFilter =
        filter === "all" ||
        (
          filter === "active" &&
          task.completedAt === null
        ) ||
        (
          filter === "completed" &&
          task.completedAt !== null
        );


      const matchesSearch =
        task.title
          .toLowerCase()
          .includes(
            search.toLowerCase()
          );


      return (
        matchesFilter &&
        matchesSearch
      );
    });



  const completedCount =
    tasks.filter(
      (task) =>
        task.completedAt !== null
    ).length;



  return (
    <>
      <Header />

      <main className={styles.page}>

        <div className={styles.container}>

          <div className={styles.top}>

            <div>
              <h1>
                My Tasks
              </h1>

              <p>
                {tasks.length} total ·{" "}
                {completedCount} completed
              </p>
            </div>


            <button
              className={
                styles.newButton
              }
              onClick={() =>
                setShowForm(true)
              }
            >
              + New task
            </button>

          </div>



          <div className={styles.searchWrapper}>
            <span className={styles.searchIcon}>
              🔍
            </span>

            <input
              className={styles.search}
              placeholder="Search tasks..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
            />
          </div>



          <div className={styles.filters}>

            {(
              [
                "all",
                "active",
                "completed",
              ] as Filter[]
            ).map((item) => (

              <button
                key={item}
                className={
                  filter === item
                    ? styles.activeFilter
                    : ""
                }
                onClick={() =>
                  setFilter(item)
                }
              >
                {item}
              </button>

            ))}

          </div>



          {showForm && (
            <TaskForm
              onSubmit={
                handleCreateTask
              }
              onCancel={() =>
                setShowForm(false)
              }
            />
          )}



          {loading ? (

            <div>
              Loading...
            </div>

          ) : (

            <div className={styles.list}>

              {visibleTasks.map(
                (task) => (

                <TaskCard
                  key={task.id}
                  task={task}
                  onToggleComplete={
                    handleToggleComplete
                  }
                  onUpdate={
                    handleUpdateTask
                  }
                  onDelete={
                    handleDeleteTask
                  }
                  onDragStart={
                    handleDragStart
                  }
                  onDrop={
                    handleDrop
                  }
                />

              ))}

            </div>

          )}

        </div>

      </main>
    </>
  );
}