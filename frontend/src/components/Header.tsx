import { useNavigate } from "react-router-dom";
import styles from "./Header.module.css";
import { generateReport } from "../api/reportApi";

export default function Header() {
  const navigate = useNavigate();

  async function handleGenerateReport() {
    await generateReport();
  }

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login", { replace: true });
  }

  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <button
          className={styles.logo}
          onClick={() => navigate("/tasks")}
          type="button"
        >
          <span className={styles.logoMark}>T</span>
          <span>Task Tracker</span>
        </button>

        <div className={styles.actions}>
          <button
            className={styles.reportButton}
            onClick={handleGenerateReport}
            type="button"
          >
            <svg
              className={styles.reportIcon}
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <path d="M4 4h16v16H4z" />
              <path d="M8 8h8" />
              <path d="M8 12h5" />
              <path d="M8 16h3" />
            </svg>

            <span>Send Me a Progress Report</span>
          </button>

          <button
            className={styles.logoutButton}
            onClick={handleLogout}
            type="button"
          >
            <svg
              className={styles.logoutIcon}
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <path d="M10 17l5-5-5-5" />
              <path d="M15 12H3" />
              <path d="M21 3v18" />
            </svg>

            <span>Logout</span>
          </button>
        </div>
      </div>
    </header>
  );
}