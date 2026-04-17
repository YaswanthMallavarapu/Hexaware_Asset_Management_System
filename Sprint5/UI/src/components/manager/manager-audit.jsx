import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  BsCheckCircle,
  BsXCircle,
  BsFunnel,
  BsPlusCircle,
} from 'react-icons/bs';

const ManagerAudit = () => {
  const [audits, setAudits] = useState([]);
  const [auditPage, setAuditPage] = useState(0);
  const [auditHasMore, setAuditHasMore] = useState(true);
  const [auditSize] = useState(5);
  const [view, setView] = useState('list');
  const [selectedAudit, setSelectedAudit] = useState(null);
  const [results, setResults] = useState([]);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [resultsPage, setResultsPage] = useState(0);
  const [resultsSize] = useState(5);
  const [resultsHasMore, setResultsHasMore] = useState(true);
  const [error, setError] = useState('');
  const [actioningId, setActioningId] = useState(null);
  const [creating, setCreating] = useState(false);

  const AUDIT_BASE = 'http://localhost:8082/api/asset-audit';

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

  const fetchAudits = async (pg) => {
    setError('');
    try {
      const res = await axios.get(
        `${AUDIT_BASE}/get-all-audit-dates?page=${pg}&size=${auditSize}`,
        config
      );
      const data = Array.isArray(res.data) ? res.data : res.data.list ?? [];
      setAudits(data);
      setAuditHasMore(data.length === auditSize);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    if (view === 'list') fetchAudits(auditPage);
  }, [auditPage, view]);

  const fetchResults = async (auditId, pg, statFilter) => {
    setError('');
    try {
      const res = await axios.get(
        `${AUDIT_BASE}/get-all/${auditId}?page=${pg}&size=${resultsSize}`,
        config
      );
      const data = Array.isArray(res.data) ? res.data : res.data.list ?? [];
      const filtered =
        statFilter === 'ALL' ? data : data.filter((r) => r.status === statFilter);
      setResults(filtered);
      setResultsHasMore(data.length === resultsSize);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    if (view === 'results' && selectedAudit) {
      fetchResults(selectedAudit.id, resultsPage, statusFilter);
    }
  }, [resultsPage, statusFilter, view, selectedAudit]);

  const handleCreateAudit = async () => {
    setCreating(true);
    setError('');
    try {
      await axios.post(`${AUDIT_BASE}/create`, {}, config);
      setAuditPage(0);
      fetchAudits(0);
    } catch (err) {
      handleError(err);
    } finally {
      setCreating(false);
    }
  };

  const openAudit = (audit) => {
    setSelectedAudit(audit);
    setResultsPage(0);
    setStatusFilter('ALL');
    setView('results');
  };

  const handleBack = () => {
    setView('list');
    setResults([]);
    setSelectedAudit(null);
  };

  const handleStatusFilterChange = (val) => {
    setStatusFilter(val);
    setResultsPage(0);
  };

  const handleVerify = async (resultId) => {
    setActioningId(resultId);
    setError('');
    try {
      await axios.post(
        `${AUDIT_BASE}/audit/${resultId}`,
        { status: 'VERIFIED' },
        config
      );
      fetchResults(selectedAudit.id, resultsPage, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
    }
  };

  const handleReject = async (resultId) => {
    setActioningId(resultId);
    setError('');
    try {
      await axios.post(
        `${AUDIT_BASE}/audit/${resultId}`,
        { status: 'REJECTED' },
        config
      );
      fetchResults(selectedAudit.id, resultsPage, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
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

  const formatDateTime = (date) => {
    if (!date) return '—';
    return new Date(date).toLocaleString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const auditStatusStyle = (status) => {
    switch (status) {
      case 'PENDING':
        return { bg: '#fffbeb', color: '#d97706', label: '⟳ Pending' };
      case 'VERIFIED':
        return { bg: '#f0fdf4', color: '#16a34a', label: '✓ Verified' };
      case 'REJECTED':
        return { bg: '#fef2f2', color: '#dc2626', label: '✕ Rejected' };
      default:
        return { bg: '#f1f5f9', color: '#475569', label: status };
    }
  };

  const statusFilterButtons = [
    { label: 'All',      value: 'ALL'      },
    { label: 'Pending',  value: 'PENDING'  },
    { label: 'Verified', value: 'VERIFIED' },
    { label: 'Rejected', value: 'REJECTED' },
  ];

  // ─── LIST VIEW ───────────────────────────────────────────────────────────────
  if (view === 'list') {
    return (
      <div
        className="container-fluid py-4 px-4"
        style={{ backgroundColor: '#f0f4f8', minHeight: '100vh' }}
      >
        {/* Header */}
        <div className="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-3">
          <div>
            <h5 className="fw-bold mb-0" style={{ color: '#1e293b' }}>
              Asset Audits
            </h5>
            <p className="text-muted small mb-0">
              View all audits and drill into their results
            </p>
          </div>
          <button
            onClick={handleCreateAudit}
            disabled={creating}
            className="btn btn-sm d-flex align-items-center gap-2"
            style={{
              backgroundColor: creating ? '#94a3b8' : '#2563eb',
              color: '#fff',
              border: 'none',
              borderRadius: '8px',
              padding: '8px 16px',
              fontSize: '0.85rem',
              fontWeight: 500,
              cursor: creating ? 'not-allowed' : 'pointer',
            }}
          >
            <BsPlusCircle size={14} />
            {creating ? 'Creating...' : 'Create New Audit'}
          </button>
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
              style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1rem', color: '#dc2626' }}
            >✕</button>
          </div>
        )}

        {/* Table Card */}
        <div
          className="card border-0"
          style={{ borderRadius: '16px', boxShadow: '0 2px 12px rgba(0,0,0,0.07)' }}
        >
          <div className="card-body p-0">
            <table className="table mb-0" style={{ borderRadius: '16px', overflow: 'hidden' }}>
              <thead style={{ backgroundColor: '#f8fafc' }}>
                <tr>
                  {['#', 'AUDIT ID', 'CREATED ON', 'MANAGER', 'ACTION'].map((h) => (
                    <th
                      key={h}
                      className="px-4 py-3 text-muted fw-semibold"
                      style={{ fontSize: '0.8rem', borderBottom: '1px solid #e2e8f0' }}
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {audits.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-5">
                      No audits found
                    </td>
                  </tr>
                ) : (
                  audits.map((audit, index) => (
                    <tr
                      key={audit.id}
                      style={{ transition: 'background 0.15s' }}
                      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#f8fafc')}
                      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
                    >
                      <td className="px-4 py-3 text-muted" style={{ fontSize: '0.9rem', borderBottom: '1px solid #f1f5f9' }}>
                        {auditPage * auditSize + index + 1}
                      </td>
                      <td className="px-4 py-3" style={{ fontSize: '0.9rem', fontWeight: 500, color: '#1e293b', borderBottom: '1px solid #f1f5f9' }}>
                        #{audit.id}
                      </td>
                      <td className="px-4 py-3" style={{ fontSize: '0.9rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {formatDateTime(audit.auditDate)}
                      </td>
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        {audit.managerId ? (
                          <div style={{ fontSize: '0.78rem', color: '#94a3b8' }}>
                            ID: {audit.managerId}
                          </div>
                        ) : (
                          <span className="text-muted" style={{ fontSize: '0.85rem' }}>—</span>
                        )}
                      </td>
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <button
                          onClick={() => openAudit(audit)}
                          className="btn btn-sm"
                          style={{
                            backgroundColor: '#eff6ff',
                            color: '#1d4ed8',
                            border: '1px solid #bfdbfe',
                            borderRadius: '8px',
                            fontSize: '0.82rem',
                            fontWeight: 500,
                          }}
                        >
                          View Results
                        </button>
                      </td>
                    </tr>
                  ))
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
              Page {auditPage + 1}
            </span>
            <div className="d-flex align-items-center gap-2">
              <button
                onClick={() => { if (auditPage > 0) setAuditPage((p) => p - 1); }}
                disabled={auditPage === 0}
                style={{
                  border: '1px solid #e2e8f0',
                  borderRadius: '8px',
                  padding: '6px 12px',
                  backgroundColor: auditPage === 0 ? '#f8fafc' : '#fff',
                  color: auditPage === 0 ? '#cbd5e1' : '#475569',
                  cursor: auditPage === 0 ? 'not-allowed' : 'pointer',
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
                {auditPage + 1}
              </span>
              <button
                onClick={() => { if (auditHasMore) setAuditPage((p) => p + 1); }}
                disabled={!auditHasMore}
                style={{
                  border: '1px solid #e2e8f0',
                  borderRadius: '8px',
                  padding: '6px 12px',
                  backgroundColor: !auditHasMore ? '#f8fafc' : '#fff',
                  color: !auditHasMore ? '#cbd5e1' : '#475569',
                  cursor: !auditHasMore ? 'not-allowed' : 'pointer',
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
  }

  // ─── RESULTS VIEW ────────────────────────────────────────────────────────────
  return (
    <div
      className="container-fluid py-4 px-4"
      style={{ backgroundColor: '#f0f4f8', minHeight: '100vh' }}
    >
      {/* Header */}
      <div className="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-3">
        <div>
          <div className="d-flex align-items-center gap-2 mb-1">
            <button
              onClick={handleBack}
              className="btn btn-sm"
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '4px 10px',
                fontSize: '0.82rem',
                color: '#475569',
                backgroundColor: '#fff',
              }}
            >
              ← Back
            </button>
            <h5 className="fw-bold mb-0" style={{ color: '#1e293b' }}>
              Audit #{selectedAudit.id} — Results
            </h5>
          </div>
          <p className="text-muted small mb-0">
            {formatDateTime(selectedAudit.auditDate)}
          </p>
        </div>
      </div>

      {/* Filters Row */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <div className="d-flex align-items-center gap-2">
          <BsFunnel size={14} className="text-muted" />
          <span className="text-muted" style={{ fontSize: '0.8rem', fontWeight: 500 }}>
            Status:
          </span>
          {statusFilterButtons.map((btn) => (
            <button
              key={btn.value}
              onClick={() => handleStatusFilterChange(btn.value)}
              style={{
                border: statusFilter === btn.value ? 'none' : '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 14px',
                fontSize: '0.83rem',
                fontWeight: 500,
                cursor: 'pointer',
                backgroundColor: statusFilter === btn.value ? '#2563eb' : '#fff',
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
            style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1rem', color: '#dc2626' }}
          >✕</button>
        </div>
      )}

      {/* Table Card */}
      <div
        className="card border-0"
        style={{ borderRadius: '16px', boxShadow: '0 2px 12px rgba(0,0,0,0.07)' }}
      >
        <div className="card-body p-0">
          <table className="table mb-0" style={{ borderRadius: '16px', overflow: 'hidden' }}>
            <thead style={{ backgroundColor: '#f8fafc' }}>
              <tr>
                {['#', 'ASSET', 'ALLOCATION ID', 'AUDIT DATE', 'STATUS', 'ACTIONS'].map((h) => (
                  <th
                    key={h}
                    className="px-4 py-3 text-muted fw-semibold"
                    style={{ fontSize: '0.8rem', borderBottom: '1px solid #e2e8f0' }}
                  >
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {results.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-5">
                    No results found
                  </td>
                </tr>
              ) : (
                results.map((r, index) => {
                  const sStyle = auditStatusStyle(r.status);
                  const isActioning = actioningId === r.id;
                  return (
                    <tr
                      key={r.id}
                      style={{ transition: 'background 0.15s' }}
                      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#f8fafc')}
                      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
                    >
                      <td className="px-4 py-3 text-muted" style={{ fontSize: '0.9rem', borderBottom: '1px solid #f1f5f9' }}>
                        {resultsPage * resultsSize + index + 1}
                      </td>
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <div className="fw-semibold" style={{ fontSize: '0.9rem', color: '#1e293b' }}>
                          {r.assetName}
                        </div>
                        <div style={{ fontSize: '0.78rem', color: '#94a3b8' }}>
                          ID: {r.assetId}
                        </div>
                      </td>
                      <td className="px-4 py-3" style={{ fontSize: '0.9rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {r.assetAllocationId}
                      </td>
                      <td className="px-4 py-3" style={{ fontSize: '0.85rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {formatDate(r.auditDate)}
                      </td>
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
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <div className="d-flex gap-2 flex-wrap">
                          {r.status === 'PENDING' && (
                            <>
                              <button
                                onClick={() => handleVerify(r.id)}
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
                                {isActioning ? 'Processing...' : 'Verify'}
                              </button>
                              <button
                                onClick={() => handleReject(r.id)}
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
                          {(r.status === 'VERIFIED' || r.status === 'REJECTED') && (
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
            Page {resultsPage + 1}
          </span>
          <div className="d-flex align-items-center gap-2">
            <button
              onClick={() => { if (resultsPage > 0) setResultsPage((p) => p - 1); }}
              disabled={resultsPage === 0}
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 12px',
                backgroundColor: resultsPage === 0 ? '#f8fafc' : '#fff',
                color: resultsPage === 0 ? '#cbd5e1' : '#475569',
                cursor: resultsPage === 0 ? 'not-allowed' : 'pointer',
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
              {resultsPage + 1}
            </span>
            <button
              onClick={() => { if (resultsHasMore) setResultsPage((p) => p + 1); }}
              disabled={!resultsHasMore}
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 12px',
                backgroundColor: !resultsHasMore ? '#f8fafc' : '#fff',
                color: !resultsHasMore ? '#cbd5e1' : '#475569',
                cursor: !resultsHasMore ? 'not-allowed' : 'pointer',
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

export default ManagerAudit;