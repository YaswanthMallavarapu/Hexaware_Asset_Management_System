import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  BsChevronLeft,
  BsChevronRight,
  BsFunnel,
  BsSend
} from "react-icons/bs";
import "../../styles/employee-assets.css";

const EmployeeAssets = () => {
  const [assets, setAssets] = useState([]);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [hasMore, setHasMore] = useState(true);
  const [requestingId, setRequestingId] = useState(null);
  const [errmsg,setErrmsg]=useState(undefined)

  const BASE = "http://localhost:8082/api/asset";

  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  const fetchAssets = async () => {
    setErrmsg(undefined)
    try {
      let url =
      statusFilter === "ALL"
        ? `${BASE}/get-all?page=${page}&size=${size}`
        : `${BASE}/get/status/${statusFilter}?page=${page}&size=${size}`;

    const res = await axios.get(url, config);
    setAssets(res.data.list);
    setHasMore(page + 1 < res.data.totalPages);
    } catch (error) {
      handleError(error)
    }
  };

  useEffect(() => {
    fetchAssets();
    
  }, [page, statusFilter]);

  const handleRequest = async (id) => {
    try {
      setErrmsg(undefined)
      setRequestingId(id);
      await axios.post(`http://localhost:8082/api/asset-request/add/${id}`, {}, config);
      fetchAssets();
    } 
    catch(error){
      handleError(error)
    }
      finally {
      setRequestingId(null);
    }
  };

  const handleError = (err) => {
    if (err.response?.status === 401) {
      setErrmsg('Unauthorized. Please log in again.');
    } else if (err.response?.status === 403) {
      setErrmsg('Access denied. You do not have permission.');
    } else if(err.response?.status==302) {
      setErrmsg('You have already requested this asset.');
    }
    console.error(err);
  };

  const statusButtons = [
    { label: "All", value: "ALL" },
    { label: "Available", value: "AVAILABLE" },
    { label: "Allocated", value: "ALLOCATED" },
    { label: "In Service", value: "IN_SERVICE" },
    { label: "Retired", value: "RETIRED" },
  ];

  return (
    <div className="container-fluid py-4 px-4 employee-assets-wrapper">

      {/* Header */}
      <div className="mb-4">
        <h5 className="fw-bold mb-0">Assets</h5>
        <p className="text-muted small mb-0">
          View and request organization assets
        </p>
      </div>

      {/* Filter Buttons */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <BsFunnel size={14} className="text-muted" />
        {statusButtons.map((btn) => (
          <button
            key={btn.value}
            onClick={() => {
              setStatusFilter(btn.value);
              setPage(0);
            }}
            className={`filter-btn ${
              statusFilter === btn.value ? "active-filter" : ""
            }`}
          >
            {btn.label}
          </button>
        ))}
      </div>


      {/* Error Alert */}
      {errmsg && (
        <div
          className="alert alert-danger py-2 px-3 mb-3 d-flex align-items-center justify-content-between"
          style={{ borderRadius: '10px', fontSize: '0.85rem' }}
        >
          {errmsg}
          <button
            onClick={() => setErrmsg('')}
            style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1rem', color: '#dc2626' }}
          >✕</button>
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
                <th>Model</th>
                <th>Category</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {assets.length === 0 ? (
                <tr>
                  <td colSpan="6" className="text-center py-5 text-muted">
                    No assets found
                  </td>
                </tr>
              ) : (
                assets.map((asset, index) => (
                  <tr key={asset.id}>
                    <td>{page * size + index + 1}</td>
                    <td className="fw-semibold">{asset.assetName}</td>
                    <td>{asset.assetModel}</td>
                    <td>{asset.categoryName}</td>
                    <td>
                      <span
                        className={`status-badge status-${asset.assetStatus.toLowerCase()}`}
                      >
                        {asset.assetStatus}
                      </span>
                    </td>
                    <td>
                      {asset.assetStatus === "AVAILABLE" ? (
                        <button
                          className="btn request-btn btn-sm"
                          disabled={requestingId === asset.id}
                          onClick={() => handleRequest(asset.id)}
                        >
                          <BsSend size={14} />
                          {requestingId === asset.id
                            ? "Requesting..."
                            : "Request"}
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
            Page {page + 1}
          </span>

          <div className="d-flex gap-2">
            <button
              className="pagination-btn"
              disabled={page === 0}
              onClick={() => setPage(page - 1)}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <button
              className="pagination-btn"
              disabled={!hasMore}
              onClick={() => setPage(page + 1)}
            >
              Next <BsChevronRight size={14} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EmployeeAssets;