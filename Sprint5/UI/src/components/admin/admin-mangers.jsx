import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BsCheckCircle, BsChevronLeft, BsChevronRight, BsFunnel } from 'react-icons/bs';

const AdminManagers = () => {

  const [managers, setManagers] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState('');
  const [approvingId, setApprovingId] = useState(null);

  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token")
    }
  };

  const handleError = (err) => {
    if (err.response?.status === 401) {
      setError('Unauthorized. Please log in again.');
    } else if (err.response?.status === 403) {
      setError('Access denied. You do not have permission.');
    } else {
      setError('Something went wrong. Please try again.');
    }
    console.error(err);
  };

  // Handler 1 — GET ALL managers
  const fetchAllManagers = async (currentPage) => {
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8082/api/manager/get-all?page=${currentPage}&size=${size}`,
        config
      );
      setManagers(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  // Handler 2 — GET FILTERED managers by AccountStatus enum
  const fetchManagersByStatus = async (currentPage, status) => {
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8082/api/manager/get-all/status/${status}?page=${currentPage}&size=${size}`,
        config
      );
      setManagers(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    if (filter === 'ALL') {
      fetchAllManagers(page);
    } else if (filter === 'APPROVED') {
      fetchManagersByStatus(page, 'APPROVED');
    } else if (filter === 'PENDING') {
      fetchManagersByStatus(page, 'PENDING');
    }
  }, [page, filter]);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setPage(0);
  };

  const handleApprove = async (id) => {
    setApprovingId(id);
    setError('');
    try {
      await axios.put(`http://localhost:8082/api/admin/approve-manager/${id}`, {}, config);
      if (filter === 'ALL') fetchAllManagers(page);
      else if (filter === 'APPROVED') fetchManagersByStatus(page, 'APPROVED');
      else if (filter === 'PENDING') fetchManagersByStatus(page, 'PENDING');
    } catch (err) {
      handleError(err);
    } finally {
      setApprovingId(null);
    }
  };

  const handleNext = () => { if (hasMore) setPage(prev => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage(prev => prev - 1); };

  const filterButtons = [
    { label: 'All', value: 'ALL' },
    { label: 'Approved', value: 'APPROVED' },
    { label: 'Pending', value: 'PENDING' },
  ];

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Managers</h5>
          <p className="text-muted small mb-0">View and approve manager accounts</p>
        </div>

        {/* Filter Buttons */}
        <div className="d-flex align-items-center gap-2">
          <BsFunnel size={16} className="text-muted" />
          {filterButtons.map(btn => (
            <button
              key={btn.value}
              onClick={() => handleFilterChange(btn.value)}
              style={{
                border: filter === btn.value ? "none" : "1px solid #e2e8f0",
                borderRadius: "8px",
                padding: "6px 14px",
                fontSize: "0.83rem",
                fontWeight: 500,
                cursor: "pointer",
                backgroundColor: filter === btn.value ? "#2563eb" : "#fff",
                color: filter === btn.value ? "#fff" : "#475569",
                transition: "all 0.15s",
              }}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>

      {/* Error Alert */}
      {error && (
        <div className="alert alert-danger py-2 px-3 mb-3 d-flex align-items-center justify-content-between"
          style={{ borderRadius: "10px", fontSize: "0.85rem" }}>
          {error}
          <button
            onClick={() => setError('')}
            style={{ background: "none", border: "none", cursor: "pointer", fontSize: "1rem", color: "#dc2626" }}
          >✕</button>
        </div>
      )}

      {/* Table Card */}
      <div className="card border-0" style={{ borderRadius: "16px", boxShadow: "0 2px 12px rgba(0,0,0,0.07)" }}>
        <div className="card-body p-0">
          <table className="table mb-0" style={{ borderRadius: "16px", overflow: "hidden" }}>
            <thead style={{ backgroundColor: "#f8fafc" }}>
              <tr>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>#</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>FULL NAME</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>USER ID</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>STATUS</th>
                <th className="px-4 py-3 text-muted fw-semibold" style={{ fontSize: "0.8rem", borderBottom: "1px solid #e2e8f0" }}>ACTIONS</th>
              </tr>
            </thead>
            <tbody>
              {managers.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center text-muted py-5">No managers found</td>
                </tr>
              ) : (
                managers.map((manager, index) => (
                  <tr
                    key={manager.id}
                    style={{ transition: "background 0.15s" }}
                    onMouseEnter={e => e.currentTarget.style.backgroundColor = "#f8fafc"}
                    onMouseLeave={e => e.currentTarget.style.backgroundColor = "transparent"}
                  >
                    <td className="px-4 py-3 text-muted" style={{ fontSize: "0.9rem", borderBottom: "1px solid #f1f5f9" }}>
                      {page * size + index + 1}
                    </td>
                    <td className="px-4 py-3 fw-semibold" style={{ fontSize: "0.9rem", color: "#1e293b", borderBottom: "1px solid #f1f5f9" }}>
                      {manager.fullName}
                    </td>
                    <td className="px-4 py-3" style={{ fontSize: "0.9rem", color: "#475569", borderBottom: "1px solid #f1f5f9" }}>
                      <span className="px-2 py-1 rounded-2" style={{ backgroundColor: "#f1f5f9", color: "#475569", fontSize: "0.8rem", fontWeight: 500 }}>
                        #{manager.userId}
                      </span>
                    </td>
                    <td className="px-4 py-3" style={{ borderBottom: "1px solid #f1f5f9" }}>
                      {manager.accountStatus === 'APPROVED' ? (
                        <span className="px-2 py-1 rounded-2" style={{ backgroundColor: "#f0fdf4", color: "#16a34a", fontSize: "0.8rem", fontWeight: 500 }}>
                          ✓ Approved
                        </span>
                      ) : (
                        <span className="px-2 py-1 rounded-2" style={{ backgroundColor: "#fffbeb", color: "#d97706", fontSize: "0.8rem", fontWeight: 500 }}>
                          ⏳ Pending
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-3" style={{ borderBottom: "1px solid #f1f5f9" }}>
                      {manager.accountStatus !== 'APPROVED' ? (
                        <button
                          onClick={() => handleApprove(manager.id)}
                          disabled={approvingId === manager.id}
                          className="btn btn-sm d-flex align-items-center gap-1"
                          style={{
                            backgroundColor: "#f0fdf4",
                            color: "#16a34a",
                            border: "1px solid #bbf7d0",
                            borderRadius: "8px",
                            fontSize: "0.82rem",
                            fontWeight: 500,
                          }}
                        >
                          <BsCheckCircle size={14} />
                          {approvingId === manager.id ? "Approving..." : "Approve"}
                        </button>
                      ) : (
                        <span className="text-muted" style={{ fontSize: "0.82rem" }}>—</span>
                      )}
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

            <span style={{
              width: 32, height: 32,
              display: "flex", alignItems: "center", justifyContent: "center",
              backgroundColor: "#2563eb", color: "#fff",
              borderRadius: "8px", fontSize: "0.85rem", fontWeight: 600,
            }}>
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
    </div>
  );
};

export default AdminManagers;