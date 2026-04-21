import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { BsPlusCircle } from 'react-icons/bs';
import '../../styles/audit.css';

const AUDIT_BASE = 'http://localhost:8082/api/asset-audit';
const PAGE_SIZE = 5;

const ManagerAudit = () => {
  const navigate = useNavigate();

  const [audits, setAudits]       = useState([]);
  const [page, setPage]           = useState(0);
  const [hasMore, setHasMore]     = useState(true);
  const [creating, setCreating]   = useState(false);
  const [error, setError]         = useState('');

  const authConfig = {
    headers: { Authorization: 'Bearer ' + localStorage.getItem('token') },
  };

  const handleError = (err) => {
    if (err.response?.status === 401) setError('Unauthorized. Please log in again.');
    else if (err.response?.status === 403) setError('Access denied.');
    else setError('Something went wrong. Please try again.');
  };

  const fetchAudits = async (pg) => {
    setError('');
    try {
      const res  = await axios.get(
        `${AUDIT_BASE}/get-all-audit-dates?page=${pg}&size=${PAGE_SIZE}`,
        authConfig
      );
      const data = res.data?.list ?? (Array.isArray(res.data) ? res.data : []);
      setAudits(data);
      setHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => { fetchAudits(page); }, [page]);

  const handleCreate = async () => {
    setCreating(true);
    setError('');
    try {
      await axios.post(`${AUDIT_BASE}/create`, {}, authConfig);
      setPage(0);
      fetchAudits(0);
    } catch (err) {
      handleError(err);
    } finally {
      setCreating(false);
    }
  };

  const formatDateTime = (date) => {
    if (!date) return '—';
    return new Date(date).toLocaleString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  };

  return (
    <div className="audit-page">
      {/* Header */}
      <div className="audit-header">
        <div>
          <h5 className="audit-title">Asset Audits</h5>
          <p className="audit-subtitle">View all audits and drill into their results</p>
        </div>
        <button className="btn-create-audit" onClick={handleCreate} disabled={creating}>
          <BsPlusCircle size={14} />
          {creating ? 'Creating...' : 'Create New Audit'}
        </button>
      </div>

      {/* Error */}
      {error && (
        <div className="alert alert-danger audit-error">
          {error}
          <button className="btn-error-close" onClick={() => setError('')}>✕</button>
        </div>
      )}

      {/* Table Card */}
      <div className="card audit-card">
        <div className="card-body p-0">
          <table className="table mb-0 audit-table">
            <thead>
              <tr>
                {['#', 'Audit ID', 'Created On', 'Manager', 'Action'].map((h) => (
                  <th key={h}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {audits.length === 0 ? (
                <tr>
                  <td colSpan={5} className="empty-state">No audits found</td>
                </tr>
              ) : (
                audits.map((audit, i) => (
                   
                  <tr key={audit.id}>
                    <td className="cell-index">{page * PAGE_SIZE + i + 1}</td>
                    <td className="cell-id">#{audit.id}</td>
                    <td className="cell-muted">{formatDateTime(audit.auditDate)}</td>
                    {console.log(audit)}
                    <td>
                      {audit.managerId
                        ? <span className="cell-sub">ID: {audit.managerId}</span>
                        : <span className="cell-muted">—</span>}
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-view-results"
                        onClick={() => navigate(`/manager/audit-results/${audit.id}`)}
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

        {/* Pagination */}
        <div className="audit-pagination">
          <span className="pagination-info">Page {page + 1}</span>
          <div className="pagination-controls">
            <button className="btn-page" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
              ‹ Prev
            </button>
            <span className="page-indicator">{page + 1}</span>
            <button className="btn-page" disabled={!hasMore} onClick={() => setPage((p) => p + 1)}>
              Next ›
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManagerAudit;