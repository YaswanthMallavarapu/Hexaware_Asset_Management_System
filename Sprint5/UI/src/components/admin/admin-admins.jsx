import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BsPersonPlus, BsTrash, BsX, BsChevronLeft, BsChevronRight } from 'react-icons/bs';

const AdminAdmins = () => {

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [admins, setAdmins] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [hasMore, setHasMore] = useState(true);

  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token")
    }
  };

  const fetchAdmins = async (currentPage) => {
    try {
      const res = await axios.get(
        `http://localhost:8082/api/admin/get-all?page=${currentPage}&size=${size}`,
        config
      );
      const data = res.data;
      setAdmins(data);
      // if returned list is less than page size, no more pages
      setHasMore(data.length === size);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchAdmins(page);
  }, [page]);

  const handleNext = () => {
    if (hasMore) setPage(prev => prev + 1);
  };

  const handlePrev = () => {
    if (page > 0) setPage(prev => prev - 1);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await axios.post("http://localhost:8082/api/admin/add", {
        firstName, lastName, username, password
      }, config);
      setFirstName('');
      setLastName('');
      setUsername('');
      setPassword('');
      setShowModal(false);
      fetchAdmins(page);
    } catch (err) {
      setError('Failed to add admin. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>

      {/* Header Row */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Admins</h5>
          <p className="text-muted small mb-0">Manage all admin accounts</p>
        </div>
        <button
          className="btn d-flex align-items-center gap-2"
          style={{
            backgroundColor: "#2563eb",
            color: "#fff",
            borderRadius: "10px",
            padding: "8px 18px",
            fontWeight: 500,
            border: "none",
          }}
          onClick={() => setShowModal(true)}
        >
          <BsPersonPlus size={18} />
          Add Admin
        </button>
      </div>

      {/* Table Card */}
      <div className="card border-0" style={{ borderRadius: "16px", boxShadow: "0 2px 12px rgba(0,0,0,0.07)" }}>
        <div className="card-body p-0">
          <table className="table mb-0" style={{ borderRadius: "16px", overflow: "hidden" }}>
            <thead style={{ backgroundColor: "#f8fafc" }}>
              <tr>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>#</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>FIRST NAME</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>LAST NAME</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>USERNAME</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>ACTIONS</th>
              </tr>
            </thead>
            <tbody>
              {admins.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center text-muted py-5">No admins found</td>
                </tr>
              ) : (
                admins.map((admin, index) => (
                  <tr key={admin.id}
                    style={{ transition: "background 0.15s" }}
                    onMouseEnter={e => e.currentTarget.style.backgroundColor = "#f8fafc"}
                    onMouseLeave={e => e.currentTarget.style.backgroundColor = "transparent"}
                  >
                    <td className="px-4 py-3 text-muted" style={{ fontSize: "0.9rem", borderBottom: "1px solid #f1f5f9" }}>
                      {page * size + index + 1}
                    </td>
                    <td className="px-4 py-3 fw-semibold" style={{ fontSize: "0.9rem", color: "#1e293b", borderBottom: "1px solid #f1f5f9" }}>{admin.firstName}</td>
                    <td className="px-4 py-3" style={{ fontSize: "0.9rem", color: "#475569", borderBottom: "1px solid #f1f5f9" }}>{admin.lastName}</td>
                    <td className="px-4 py-3" style={{ fontSize: "0.9rem", color: "#475569", borderBottom: "1px solid #f1f5f9" }}>
                      <span className="px-2 py-1 rounded-2" style={{ backgroundColor: "#eff6ff", color: "#2563eb", fontSize: "0.8rem", fontWeight: 500 }}>
                        @{admin.username}
                      </span>
                    </td>
                    <td className="px-4 py-3" style={{ borderBottom: "1px solid #f1f5f9" }}>
                      
                        <span className="text-muted" style={{ fontSize: "0.82rem" }}>—</span>
                      
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Footer */}
        <div className="d-flex justify-content-between align-items-center px-4 py-3" style={{ borderTop: "1px solid #e2e8f0" }}>
          <span className="text-muted" style={{ fontSize: "0.85rem" }}>
            Page {page + 1}
          </span>
          <div className="d-flex align-items-center gap-2">
            <button
              onClick={handlePrev}
              disabled={page === 0}
              style={{
                border: "1px solid #e2e8f0",
                borderRadius: "8px",
                padding: "6px 12px",
                backgroundColor: page === 0 ? "#f8fafc" : "#fff",
                color: page === 0 ? "#cbd5e1" : "#475569",
                cursor: page === 0 ? "not-allowed" : "pointer",
                display: "flex",
                alignItems: "center",
                gap: "4px",
                fontSize: "0.85rem",
                fontWeight: 500,
              }}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <span
              style={{
                width: 32, height: 32,
                display: "flex", alignItems: "center", justifyContent: "center",
                backgroundColor: "#2563eb", color: "#fff",
                borderRadius: "8px", fontSize: "0.85rem", fontWeight: 600,
              }}
            >
              {page + 1}
            </span>

            <button
              onClick={handleNext}
              disabled={!hasMore}
              style={{
                border: "1px solid #e2e8f0",
                borderRadius: "8px",
                padding: "6px 12px",
                backgroundColor: !hasMore ? "#f8fafc" : "#fff",
                color: !hasMore ? "#cbd5e1" : "#475569",
                cursor: !hasMore ? "not-allowed" : "pointer",
                display: "flex",
                alignItems: "center",
                gap: "4px",
                fontSize: "0.85rem",
                fontWeight: 500,
              }}
            >
              Next <BsChevronRight size={14} />
            </button>
          </div>
        </div>
      </div>

      {/* Modal Overlay */}
      {showModal && (
        <div
          style={{
            position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
            backgroundColor: "rgba(0,0,0,0.4)", zIndex: 1050,
            display: "flex", alignItems: "center", justifyContent: "center"
          }}
          onClick={() => setShowModal(false)}
        >
          <div
            style={{
              backgroundColor: "#fff", borderRadius: "20px", padding: "32px",
              width: "100%", maxWidth: "440px", boxShadow: "0 20px 60px rgba(0,0,0,0.15)",
            }}
            onClick={e => e.stopPropagation()}
          >
            <div className="d-flex justify-content-between align-items-center mb-4">
              <div>
                <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Add New Admin</h5>
                <p className="text-muted small mb-0">Fill in the details below</p>
              </div>
              <button
                onClick={() => setShowModal(false)}
                style={{ background: "#f1f5f9", border: "none", borderRadius: "8px", padding: "6px 10px", cursor: "pointer" }}
              >
                <BsX size={20} color="#64748b" />
              </button>
            </div>

            {error && (
              <div className="alert alert-danger py-2 px-3 mb-3" style={{ borderRadius: "10px", fontSize: "0.85rem" }}>
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="row g-3">
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>First Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="John"
                    value={firstName}
                    onChange={e => setFirstName(e.target.value)}
                    required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Last Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Doe"
                    value={lastName}
                    onChange={e => setLastName(e.target.value)}
                    required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-12">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Username</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="johndoe"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-12">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Password</label>
                  <input
                    type="password"
                    className="form-control"
                    placeholder="••••••••"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
              </div>

              <div className="d-flex gap-2 mt-4">
                <button
                  type="button"
                  className="btn w-50"
                  onClick={() => setShowModal(false)}
                  style={{ borderRadius: "10px", border: "1.5px solid #e2e8f0", color: "#64748b", fontWeight: 500 }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn w-50"
                  disabled={loading}
                  style={{ borderRadius: "10px", backgroundColor: "#2563eb", color: "#fff", fontWeight: 500, border: "none" }}
                >
                  {loading ? "Adding..." : "Add Admin"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default AdminAdmins;