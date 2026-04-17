import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  BsCheckCircle,
  BsXCircle,
  BsTools,
  BsChevronLeft,
  BsChevronRight,
  BsFunnel,
} from 'react-icons/bs';

const ManagerServiceRequests = () => {
  const [requests, setRequests] = useState([]);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState('');
  const [actioningId, setActioningId] = useState(null);

  const BASE = 'http://localhost:8082/api/service-request';

  const config = {
    headers: {
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    },
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

  const fetchRequests = async (currentPage, statFilter) => {
    setError('');
    try {
      let res;
      if (statFilter !== 'ALL') {
        res = await axios.get(
          `${BASE}/status/${statFilter}?page=${currentPage}&size=${size}`,
          config
        );
      } else {
        res = await axios.get(
          `${BASE}/get-all?page=${currentPage}&size=${size}`,
          config
        );
      }
      setRequests(res.data.list);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    fetchRequests(page, statusFilter);
  }, [page, statusFilter]);

  const handleStatusFilterChange = (val) => {
    setStatusFilter(val);
    setPage(0);
  };

  const handleAccept = async (id) => {
    setActioningId(id);
    setError('');
    try {
      await axios.put(`${BASE}/accept-service-request/${id}`, {}, config);
      fetchRequests(page, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
    }
  };

  const handleReject = async (id) => {
    setActioningId(id);
    setError('');
    try {
      await axios.put(`${BASE}/reject-service-request/${id}`, {}, config);
      fetchRequests(page, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
    }
  };

  const handleResolve = async (id) => {
    setActioningId(id);
    setError('');
    try {
      await axios.put(`${BASE}/resolved-service-request/${id}`, {}, config);
      fetchRequests(page, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
    }
  };

  const handleNext = () => { if (hasMore) setPage((prev) => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage((prev) => prev - 1); };

  const statusFilterButtons = [
    { label: 'All',         value: 'ALL' },
    { label: 'Open',        value: 'OPEN' },
    { label: 'In Progress', value: 'IN_PROGRESS' },
    { label: 'Resolved',    value: 'RESOLVED' },
    { label: 'Rejected',    value: 'REJECTED' },
  ];

  const serviceStatusStyle = (status) => {
    switch (status) {
      case 'OPEN':
        return { bg: '#eff6ff', color: '#1d4ed8', label: '● Open' };
      case 'IN_PROGRESS':
        return { bg: '#fffbeb', color: '#d97706', label: '⟳ In Progress' };
      case 'RESOLVED':
        return { bg: '#f0fdf4', color: '#16a34a', label: '✓ Resolved' };
      case 'REJECTED':
        return { bg: '#fef2f2', color: '#dc2626', label: '✕ Rejected' };
      default:
        return { bg: '#f1f5f9', color: '#475569', label: status };
    }
  };

  const formatDate = (date) => {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  return (
    <div
      className="container-fluid py-4 px-4"
      style={{ backgroundColor: '#f0f4f8', minHeight: '100vh' }}
    >
      {/* Header */}
      <div className="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-3">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: '#1e293b' }}>
            Service Requests
          </h5>
          <p className="text-muted small mb-0">
            Review, accept, reject, or resolve asset service requests
          </p>
        </div>
      </div>

      {/* Filters Row */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <div className="d-flex align-items-center gap-2">
          <BsFunnel size={14} className="text-muted" />
          <span
            className="text-muted"
            style={{ fontSize: '0.8rem', fontWeight: 500 }}
          >
            Status:
          </span>
          {statusFilterButtons.map((btn) => (
            <button
              key={btn.value}
              onClick={() => handleStatusFilterChange(btn.value)}
              style={{
                border:
                  statusFilter === btn.value ? 'none' : '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 14px',
                fontSize: '0.83rem',
                fontWeight: 500,
                cursor: 'pointer',
                backgroundColor:
                  statusFilter === btn.value ? '#2563eb' : '#fff',
                color: statusFilter === btn.value ? '#fff' : '#475569',
                transition: 'all 0.15s',
              }}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>

      {/* Error Alert */}
      {error && (
        <div
          className="alert alert-danger py-2 px-3 mb-3 d-flex align-items-center justify-content-between"
          style={{ borderRadius: '10px', fontSize: '0.85rem' }}
        >
          {error}
          <button
            onClick={() => setError('')}
            style={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              fontSize: '1rem',
              color: '#dc2626',
            }}
          >
            ✕
          </button>
        </div>
      )}

      {/* Table Card */}
      <div
        className="card border-0"
        style={{
          borderRadius: '16px',
          boxShadow: '0 2px 12px rgba(0,0,0,0.07)',
        }}
      >
        <div className="card-body p-0">
          <table
            className="table mb-0"
            style={{ borderRadius: '16px', overflow: 'hidden' }}
          >
            <thead style={{ backgroundColor: '#f8fafc' }}>
              <tr>
                {[
                  '#',
                  'EMPLOYEE',
                  'ASSET',
                  'DESCRIPTION',
                  'DATE',
                  'STATUS',
                  'ACTIONS',
                ].map((h) => (
                  <th
                    key={h}
                    className="px-4 py-3 text-muted fw-semibold"
                    style={{
                      fontSize: '0.8rem',
                      borderBottom: '1px solid #e2e8f0',
                    }}
                  >
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {requests.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-muted py-5">
                    No service requests found
                  </td>
                </tr>
              ) : (
                requests.map((req, index) => {
                  const sStyle = serviceStatusStyle(req.status);
                  const isActioning = actioningId === req.id;
                  return (
                    <tr
                      key={req.id}
                      style={{ transition: 'background 0.15s' }}
                      onMouseEnter={(e) =>
                        (e.currentTarget.style.backgroundColor = '#f8fafc')
                      }
                      onMouseLeave={(e) =>
                        (e.currentTarget.style.backgroundColor = 'transparent')
                      }
                    >
                      {/* # */}
                      <td
                        className="px-4 py-3 text-muted"
                        style={{ fontSize: '0.9rem', borderBottom: '1px solid #f1f5f9' }}
                      >
                        {page * size + index + 1}
                      </td>

                      {/* Employee */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <div className="fw-semibold" style={{ fontSize: '0.9rem', color: '#1e293b' }}>
                          {req.employeeName}
                        </div>
                        <div style={{ fontSize: '0.78rem', color: '#94a3b8' }}>
                          ID: {req.employeeId}
                        </div>
                      </td>

                      {/* Asset */}
                      <td className="px-4 py-3" style={{ fontSize: '0.9rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        <div style={{ fontWeight: 500 }}>{req.assetName || '—'}</div>
                        <div style={{ fontSize: '0.78rem', color: '#94a3b8' }}>
                          ID: {req.assetId}
                        </div>
                      </td>

                      {/* Description */}
                      <td className="px-4 py-3" style={{ fontSize: '0.85rem', color: '#475569', borderBottom: '1px solid #f1f5f9', maxWidth: '200px' }}>
                        <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {req.description || '—'}
                        </div>
                      </td>

                      {/* Date */}
                      <td className="px-4 py-3" style={{ fontSize: '0.85rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {formatDate(req.requestDate)}
                      </td>

                      {/* Status */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <span
                          className="px-2 py-1 rounded-2"
                          style={{
                            backgroundColor: sStyle.bg,
                            color: sStyle.color,
                            fontSize: '0.8rem',
                            fontWeight: 500,
                          }}
                        >
                          {sStyle.label}
                        </span>
                      </td>

                      {/* Actions */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <div className="d-flex gap-2 flex-wrap">

                          {/* OPEN → accept or reject */}
                          {req.status === 'OPEN' && (
                            <>
                              <button
                                onClick={() => handleAccept(req.id)}
                                disabled={isActioning}
                                className="btn btn-sm d-flex align-items-center gap-1"
                                style={{
                                  backgroundColor: '#f0fdf4',
                                  color: '#16a34a',
                                  border: '1px solid #bbf7d0',
                                  borderRadius: '8px',
                                  fontSize: '0.82rem',
                                  fontWeight: 500,
                                }}
                              >
                                <BsCheckCircle size={13} />
                                {isActioning ? 'Processing...' : 'Accept'}
                              </button>
                              <button
                                onClick={() => handleReject(req.id)}
                                disabled={isActioning}
                                className="btn btn-sm d-flex align-items-center gap-1"
                                style={{
                                  backgroundColor: '#fef2f2',
                                  color: '#dc2626',
                                  border: '1px solid #fecaca',
                                  borderRadius: '8px',
                                  fontSize: '0.82rem',
                                  fontWeight: 500,
                                }}
                              >
                                <BsXCircle size={13} />
                                {isActioning ? 'Processing...' : 'Reject'}
                              </button>
                            </>
                          )}

                          {/* IN_PROGRESS → resolve */}
                          {req.status === 'IN_PROGRESS' && (
                            <button
                              onClick={() => handleResolve(req.id)}
                              disabled={isActioning}
                              className="btn btn-sm d-flex align-items-center gap-1"
                              style={{
                                backgroundColor: '#fffbeb',
                                color: '#d97706',
                                border: '1px solid #fde68a',
                                borderRadius: '8px',
                                fontSize: '0.82rem',
                                fontWeight: 500,
                              }}
                            >
                              <BsTools size={13} />
                              {isActioning ? 'Processing...' : 'Resolve'}
                            </button>
                          )}

                          {/* RESOLVED / REJECTED → no action */}
                          {(req.status === 'RESOLVED' || req.status === 'REJECTED') && (
                            <span className="text-muted" style={{ fontSize: '0.82rem' }}>—</span>
                          )}

                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Footer */}
        <div
          className="d-flex justify-content-between align-items-center px-4 py-3"
          style={{ borderTop: '1px solid #e2e8f0' }}
        >
          <span className="text-muted" style={{ fontSize: '0.85rem' }}>
            Page {page + 1}
          </span>
          <div className="d-flex align-items-center gap-2">
            <button
              onClick={handlePrev}
              disabled={page === 0}
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 12px',
                backgroundColor: page === 0 ? '#f8fafc' : '#fff',
                color: page === 0 ? '#cbd5e1' : '#475569',
                cursor: page === 0 ? 'not-allowed' : 'pointer',
                display: 'flex', alignItems: 'center', gap: '4px',
                fontSize: '0.85rem', fontWeight: 500,
              }}
            >
              ‹ Prev
            </button>
            <span
              style={{
                width: 32, height: 32,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                backgroundColor: '#2563eb', color: '#fff',
                borderRadius: '8px', fontSize: '0.85rem', fontWeight: 600,
              }}
            >
              {page + 1}
            </span>
            <button
              onClick={handleNext}
              disabled={!hasMore}
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 12px',
                backgroundColor: !hasMore ? '#f8fafc' : '#fff',
                color: !hasMore ? '#cbd5e1' : '#475569',
                cursor: !hasMore ? 'not-allowed' : 'pointer',
                display: 'flex', alignItems: 'center', gap: '4px',
                fontSize: '0.85rem', fontWeight: 500,
              }}
            >
              Next ›
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManagerServiceRequests;