import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { getCurrentUser } from "../api/authApi";

interface Props {
  children: React.ReactNode;
}

export default function ProtectedRoute({
  children,
}: Props) {
  const [loading, setLoading] = useState(true);
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    async function checkAuth() {
      const token = localStorage.getItem("token");

      if (!token) {
        setAuthorized(false);
        setLoading(false);
        return;
      }

      try {
        await getCurrentUser(token);

        setAuthorized(true);
      } catch {
        localStorage.removeItem("token");
        setAuthorized(false);
      } finally {
        setLoading(false);
      }
    }

    checkAuth();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!authorized) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return children;
}