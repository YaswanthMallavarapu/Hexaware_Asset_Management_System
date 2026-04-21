import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { BsCheckCircle, BsXCircle, BsFunnel } from 'react-icons/bs';
import '../../styles/audit.css';

const AUDIT_BASE = 'http://localhost:8082/api/asset-audit';

const STATUS_FILTERS = [
  { label: 'All',      value: 'ALL'      },
  { label: 'Pending',  value: 'PENDING'  },
  { label: 'Verified', value: 'VERIFIED' },
  { label: 'Rejected', value: 'REJECTED' },
];

const AuditResults = () => {
  const { auditId } = useParams();
  const navigate = useNavigate();

  const [results, setResults]           = useState([]);
  const [page, setPage]                 = useState(0);
  const [hasMore, setHasMore]           = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [actioningId, setActioningId]   = useState(null);
  const [error, setError]               = useState('');
  const PAGE_SIZE = 5;

  const authConfig = {
    headers: { Authorization: 'Bearer ' + localStorage.getItem('token') },
  };

  const handleError = (err) => {
    if (err.response && err.response.status === 401) {
      setError('Unauthorized. Please log in again.');
    } else if (err.response && err.response.status === 403) {
      setError('Access denied.');
    } else {
      setError('Something went wrong. Please try again.');
    }
  };

  const fetchResults = async (pg, filter) => {
    setError('');
    try {
      let data = [];

      if (filter === 'ALL') {
        // GET /get-all/{auditId}?page=&size=  — returns a plain list
        const res = await axios.get(
          `${AUDIT_BASE}/get-all/${auditId}?page=${pg}&size=${PAGE_SIZE}`,
          authConfig
        );
        data = res.data;
        setHasMore(data.length === PAGE_SIZE);

      } else {
        const res = await axios.get(
          `${AUDIT_BASE}/status/${auditId}?page=${pg}&size=${PAGE_SIZE}&status=${filter}`,
          {
            headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
          }
        );
        data = res.data.list;
        setHasMore(pg + 1 < res.data.totalPages);
      }

      setResults(data);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    fetchResults(page, statusFilter);
  }, [page, statusFilter]);

  const handleFilterChange = (value) => {
    setStatusFilter(value);
    setPage(0);
  };

  const updateStatus = async (resultId, status) => {
    setActioningId(resultId);
    setError('');
    try {
      await axios.post(`${AUDIT_BASE}/audit/${resultId}`, { status }, authConfig);
      fetchResults(page, statusFilter);
    } catch (err) {
      handleError(err);
    } finally {
      setActioningId(null);
    }
  };

  const formatDate = (date) => {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  };

  const getStatusClassName = (status) => {
    if (status === 'PENDING')  return 'status-badge status-pending';
    if (status === 'VERIFIED') return 'status-badge status-verified';
    if (status === 'REJECTED') return 'status-badge status-rejected';
    return 'status-badge';
  };

  const getStatusLabel = (status) => {
    if (status === 'PENDING')  return '⟳ Pending';
    if (status === 'VERIFIED') return '✓ Verified';
    if (status === 'REJECTED') return '✕ Rejected';
    return status;
  };

  return (
    <div className="audit-page">

      {/* Header */}
      <div className="audit-header">
        <div>
          <div className="d-flex align-items-center gap-2 mb-1">
            <button className="btn-back" onClick={() => navigate('/manager/audit')}>← Back</button>
            <h5 className="audit-title">Audit #{auditId} — Results</h5>
          </div>
          <p className="audit-subtitle">Showing all asset results for this audit</p>
        </div>
      </div>

      {/* Filters */}
      <div className="filter-bar">
        <BsFunnel size={14} className="text-muted" />
        <span className="filter-label">Status:</span>
        {STATUS_FILTERS.map((btn) => (
          <button
            key={btn.value}
            className={statusFilter === btn.value ? 'btn-filter active' : 'btn-filter'}
            onClick={() => handleFilterChange(btn.value)}
          >
            {btn.label}
          </button>
        ))}
      </div>

      {/* Error */}
      {error && (
        <div className="alert alert-danger audit-error d-flex justify-content-between align-items-center">
          <span>{error}</span>
          <button className="btn-error-close" onClick={() => setError('')}>✕</button>
        </div>
      )}

      {/* Table Card */}
      <div className="card audit-card">
        <div className="card-body p-0">
          <table className="table mb-0 audit-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Asset</th>
                <th>Allocation ID</th>
                <th>Audit Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {results.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-5">No results found</td>
                </tr>
              ) : (
                results.map((r, i) => {
                  const isActioning = actioningId === r.id;
                  return (
                    <tr key={r.id}>
                      <td className="cell-index">{page * PAGE_SIZE + i + 1}</td>
                      <td>
                        <div className="asset-name">{r.assetName}</div>
                        <div className="cell-sub">ID: {r.assetId}</div>
                      </td>
                      <td className="cell-muted">{r.assetAllocationId}</td>
                      <td className="cell-muted">{formatDate(r.auditDate)}</td>
                      <td>
                        <span className={getStatusClassName(r.status)}>
                          {getStatusLabel(r.status)}
                        </span>
                      </td>
                      <td>
                        {r.status === 'PENDING' ? (
                          <div className="d-flex gap-2 flex-wrap">
                            <button
                              className="btn btn-sm btn-verify"
                              disabled={isActioning}
                              onClick={() => updateStatus(r.id, 'VERIFIED')}
                            >
                              <BsCheckCircle size={13} />
                              {isActioning ? ' Processing...' : ' Verify'}
                            </button>
                            <button
                              className="btn btn-sm btn-reject"
                              disabled={isActioning}
                              onClick={() => updateStatus(r.id, 'REJECTED')}
                            >
                              <BsXCircle size={13} />
                              {isActioning ? ' Processing...' : ' Reject'}
                            </button>
                          </div>
                        ) : (
                          <span className="cell-muted">—</span>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="audit-pagination">
          <span className="pagination-info">Page {page + 1}</span>
          <div className="pagination-controls">
            <button className="btn-page" disabled={page === 0} onClick={() => setPage(page - 1)}>
              ‹ Prev
            </button>
            <span className="page-indicator">{page + 1}</span>
            <button className="btn-page" disabled={!hasMore} onClick={() => setPage(page + 1)}>
              Next ›
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuditResults;