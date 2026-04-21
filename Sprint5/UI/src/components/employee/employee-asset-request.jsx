import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  BsChevronLeft,
  BsChevronRight,
  BsFunnel
} from "react-icons/bs";
import "../../styles/employee-assets.css";

const EmployeeAssetRequest = () => {

  const [requests, setRequests] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [size] = useState(5);
  const [filter, setFilter] = useState("ALL");
  const [errmsg, setErrmsg] = useState(undefined);
  const [cancellingId, setCancellingId] = useState(null);

  const BASE = "http://localhost:8082/api/asset-request";

  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  const filters = [
    { name: "All", value: "ALL" },
    { name: "Pending", value: "PENDING" },
    { name: "Approved", value: "APPROVED" },
    { name: "Rejected", value: "REJECTED" },
  ];

  const fetchAssetRequests = async () => {
    setErrmsg(undefined);
    try {
      let url =
        filter === "ALL"
          ? `${BASE}/get/user?page=${page}&size=${size}`
          : `${BASE}/get/user/status/${filter}?page=${page}&size=${size}`;

      const res = await axios.get(url, config);

      setRequests(res.data.list || []);
      setTotalPages(res.data.totalPages || 1);

    } catch (error) {
      handleError(error);
    }
  };

  useEffect(() => {
    fetchAssetRequests();
  }, [page, filter]);

  const handleCancel = async (id) => {
    try {
      setCancellingId(id);
      setErrmsg(undefined);

      await axios.delete(`${BASE}/delete/${id}`, config);

      // Optimistic UI update
      setRequests(prev => prev.filter(req => req.id !== id));

    } catch (error) {
      handleError(error);
    } finally {
      setCancellingId(null);
    }
  };

  const handleError = (err) => {
    if (err.response?.status === 401) {
      setErrmsg("Unauthorized. Please log in again.");
    } else if (err.response?.status === 403) {
      setErrmsg("Access denied.");
    } else {
      setErrmsg("Something went wrong.");
    }
    console.error(err);
  };

  const hasPrev = () => page > 0;
  const hasNext = () => page < totalPages - 1;

  const formatDate = (date) => {
  if (!date) return "—";
  return new Date(date).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

  return (
    <div className="container-fluid py-4 px-4 employee-assets-wrapper">

      {/* Header */}
      <div className="mb-4">
        <h5 className="fw-bold mb-0">My Asset Requests</h5>
        <p className="text-muted small mb-0">
          Track and manage your asset requests
        </p>
      </div>

      {/* Filter Buttons */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <BsFunnel size={14} className="text-muted" />
        {filters.map((btn) => (
          <button
            key={btn.value}
            onClick={() => {
              setFilter(btn.value);
              setPage(0);
            }}
            className={`filter-btn ${
              filter === btn.value ? "active-filter" : ""
            }`}
          >
            {btn.name}
          </button>
        ))}
      </div>

      {/* Error Alert */}
      {errmsg && (
        <div
          className="alert alert-danger py-2 px-3 mb-3 d-flex align-items-center justify-content-between"
          style={{ borderRadius: "10px", fontSize: "0.85rem" }}
        >
          {errmsg}
          <button
            onClick={() => setErrmsg("")}
            style={{
              background: "none",
              border: "none",
              cursor: "pointer",
              fontSize: "1rem",
              color: "#dc2626",
            }}
          >
            ✕
          </button>
        </div>
      )}

      {/* Table Card */}
      <div className="card asset-card">
        <div className="card-body p-0">
          <table className="table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Asset Name</th>
                <th>Requested Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.length === 0 ? (
                <tr>
                  <td colSpan="5" className="text-center py-5 text-muted">
                    No requests found
                  </td>
                </tr>
              ) : (
                requests.map((request, index) => (
                  <tr key={request.id}>
                    <td>{page * size + index + 1}</td>
                    <td className="fw-semibold">{request.assetName}</td>
                    <td>{formatDate(request.requestedDate)}</td>
                    <td>
                      <span
                        className={`status-badge status-${request.status.toLowerCase()}`}
                      >
                        {request.status}
                      </span>
                    </td>
                    <td>
                      {request.status === "PENDING" ? (
                        <button
                          className="btn request-btn btn-sm"
                          disabled={cancellingId === request.id}
                          onClick={() => handleCancel(request.id)}
                        >
                          {cancellingId === request.id
                            ? "Cancelling..."
                            : "Cancel"}
                        </button>
                      ) : (
                        <span className="text-muted">—</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="d-flex justify-content-between align-items-center px-4 py-3 border-top">
          <span className="text-muted small">
            Page {page + 1} of {totalPages}
          </span>

          <div className="d-flex gap-2">
            <button
              className="pagination-btn"
              disabled={!hasPrev()}
              onClick={() => {
                if (hasPrev()) setPage(prev => prev - 1);
              }}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <button
              className="pagination-btn"
              disabled={!hasNext()}
              onClick={() => {
                if (hasNext()) setPage(prev => prev + 1);
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

export default EmployeeAssetRequest;