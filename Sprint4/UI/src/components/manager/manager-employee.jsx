import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  BsCheckCircle,
  BsChevronLeft,
  BsChevronRight,
  BsFunnel,
  BsSearch,
} from 'react-icons/bs';

const ManagerEmployees = () => {
  const [employees, setEmployees] = useState([]);
  const [accountFilter, setAccountFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState('');
  const [approvingId, setApprovingId] = useState(null);

  const BASE = 'http://localhost:8082/api/employee';

  const config = {
    headers: {
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    },
  };

  const [accStatus, setAccStatus] = useState("")
  const [statStatus, setStatStatus] = useState("")

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

  const fetchEmployees = async (currentPage, acctFilter, statFilter) => {
    setError('');
    try {
      let url = '';
      const body = {}

      if (acctFilter !== 'ALL' && statFilter !== 'ALL') {
        // Both filters
        url = `${BASE}/filter?page=${currentPage}&size=${size}`;
        // setAccStatus(accountFilter)
        // setStatStatus(statFilter)
        body.userStatus = accountFilter
        body.employeeStatus = statFilter
      } else if (acctFilter !== 'ALL') {
        // Account status filter
        url = `${BASE}/filter?page=${currentPage}&size=${size}`;
        // setAccStatus(accountFilter)
        body.userStatus = accountFilter


      } else if (statFilter !== 'ALL') {
        // User status filter
        url = `${BASE}/filter?page=${currentPage}&size=${size}`;

        // setStatStatus(statFilter)
        body.employeeStatus = statFilter
      } else {
        // All employees
        url = `${BASE}/get-all?page=${currentPage}&size=${size}`;
        const res = await axios.get(url, config);
        setEmployees(res.data);
        setHasMore(res.data.length === size);
        return;
      }

      const res = await axios.post(url, body, config);
      setEmployees(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    fetchEmployees(page, accountFilter, statusFilter);
  }, [page, accountFilter, statusFilter]);

  const handleAccountFilterChange = (val) => {
    setAccountFilter(val);
    setPage(0);
    setSearch('');
    setSearchInput('');
  };

  const handleStatusFilterChange = (val) => {
    setStatusFilter(val);
    setPage(0);
    setSearch('');
    setSearchInput('');
  };



  const handleApprove = async (id) => {
    setApprovingId(id);
    setError('');
    try {
      await axios.put(`http://localhost:8082/api/manager/approve-employee/${id}`, {}, config);
      fetchEmployees(page, accountFilter, statusFilter, search);
    } catch (err) {
      handleError(err);
    } finally {
      setApprovingId(null);
    }
  };

  const handleNext = () => { if (hasMore) setPage((prev) => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage((prev) => prev - 1); };

  const accountFilterButtons = [
    { label: 'All', value: 'ALL' },
    { label: 'Approved', value: 'APPROVED' },
    { label: 'Pending', value: 'PENDING' },
  ];

  const userStatusButtons = [
    { label: 'All', value: 'ALL' },
    { label: 'Active', value: 'ACTIVE' },
    { label: 'On Leave', value: 'ON_LEAVE' },
    { label: 'Terminated', value: 'TERMINATED' },
    { label: 'Resigned', value: 'RESIGNED' },
  ];

  const userStatusStyle = (status) => {
    switch (status) {
      case 'ACTIVE': return { bg: '#f0fdf4', color: '#16a34a', label: '● Active' };
      case 'ON_LEAVE': return { bg: '#fffbeb', color: '#d97706', label: '⏸ On Leave' };
      case 'TERMINATED': return { bg: '#fef2f2', color: '#dc2626', label: '✕ Terminated' };
      case 'RESIGNED': return { bg: '#f8fafc', color: '#64748b', label: '↩ Resigned' };
      default: return { bg: '#f1f5f9', color: '#475569', label: status };
    }
  };

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: '#f0f4f8', minHeight: '100vh' }}>

      {/* Header */}
      <div className="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-3">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: '#1e293b' }}>Employees</h5>
          <p className="text-muted small mb-0">View and manage employee accounts</p>
        </div>


      </div>

      {/* Filters Row */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">

        {/* Account Status Filter */}
        <div className="d-flex align-items-center gap-2">
          <BsFunnel size={14} className="text-muted" />
          <span className="text-muted" style={{ fontSize: '0.8rem', fontWeight: 500 }}>Account:</span>
          {accountFilterButtons.map((btn) => (
            <button
              key={btn.value}
              onClick={() => handleAccountFilterChange(btn.value)}
              style={{
                border: accountFilter === btn.value ? 'none' : '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '6px 14px',
                fontSize: '0.83rem',
                fontWeight: 500,
                cursor: 'pointer',
                backgroundColor: accountFilter === btn.value ? '#2563eb' : '#fff',
                color: accountFilter === btn.value ? '#fff' : '#475569',
                transition: 'all 0.15s',
              }}
            >
              {btn.label}
            </button>
          ))}
        </div>

        {/* Divider */}
        <div style={{ width: '1px', height: '24px', backgroundColor: '#e2e8f0' }} />

        {/* User Status Filter */}
        <div className="d-flex align-items-center gap-2">
          <span className="text-muted" style={{ fontSize: '0.8rem', fontWeight: 500 }}>Status:</span>
          {userStatusButtons.map((btn) => (
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
                backgroundColor: statusFilter === btn.value ? '#0891b2' : '#fff',
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
      <div className="card border-0" style={{ borderRadius: '16px', boxShadow: '0 2px 12px rgba(0,0,0,0.07)' }}>
        <div className="card-body p-0">
          <table className="table mb-0" style={{ borderRadius: '16px', overflow: 'hidden' }}>
            <thead style={{ backgroundColor: '#f8fafc' }}>
              <tr>
                {['#', 'FULL NAME', 'DESIGNATION', 'CONTACT', 'USER STATUS', 'ACCOUNT', 'ACTIONS'].map((h) => (
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
              {employees.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-muted py-5">No employees found</td>
                </tr>
              ) : (
                employees.map((emp, index) => {
                  const uStyle = userStatusStyle(emp.status);
                  return (
                    <tr
                      key={emp.id}
                      style={{ transition: 'background 0.15s' }}
                      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#f8fafc')}
                      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
                    >
                      {/* # */}
                      <td className="px-4 py-3 text-muted" style={{ fontSize: '0.9rem', borderBottom: '1px solid #f1f5f9' }}>
                        {page * size + index + 1}
                      </td>

                      {/* Full Name + Gender */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <div className="fw-semibold" style={{ fontSize: '0.9rem', color: '#1e293b' }}>{emp.fullName}</div>
                        <div style={{ fontSize: '0.78rem', color: '#94a3b8' }}>{emp.gender}</div>
                      </td>

                      {/* Designation */}
                      <td className="px-4 py-3" style={{ fontSize: '0.9rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {emp.designation || '—'}
                      </td>

                      {/* Contact */}
                      <td className="px-4 py-3" style={{ fontSize: '0.85rem', color: '#475569', borderBottom: '1px solid #f1f5f9' }}>
                        {emp.contactNumber || '—'}
                      </td>

                      {/* User Status */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        <span
                          className="px-2 py-1 rounded-2"
                          style={{ backgroundColor: uStyle.bg, color: uStyle.color, fontSize: '0.8rem', fontWeight: 500 }}
                        >
                          {uStyle.label}
                        </span>
                      </td>

                      {/* Account Status */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        {emp.accountStatus === 'APPROVED' ? (
                          <span className="px-2 py-1 rounded-2" style={{ backgroundColor: '#f0fdf4', color: '#16a34a', fontSize: '0.8rem', fontWeight: 500 }}>
                            ✓ Approved
                          </span>
                        ) : (
                          <span className="px-2 py-1 rounded-2" style={{ backgroundColor: '#fffbeb', color: '#d97706', fontSize: '0.8rem', fontWeight: 500 }}>
                            ⏳ Pending
                          </span>
                        )}
                      </td>

                      {/* Actions */}
                      <td className="px-4 py-3" style={{ borderBottom: '1px solid #f1f5f9' }}>
                        {emp.accountStatus !== 'APPROVED' ? (
                          <button
                            onClick={() => handleApprove(emp.id)}
                            disabled={approvingId === emp.id}
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
                            <BsCheckCircle size={14} />
                            {approvingId === emp.id ? 'Approving...' : 'Approve'}
                          </button>
                        ) : (
                          <span className="text-muted" style={{ fontSize: '0.82rem' }}>—</span>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Footer */}
        <div className="d-flex justify-content-between align-items-center px-4 py-3" style={{ borderTop: '1px solid #e2e8f0' }}>
          <span className="text-muted" style={{ fontSize: '0.85rem' }}>Page {page + 1}</span>
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
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                fontSize: '0.85rem',
                fontWeight: 500,
              }}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <span style={{
              width: 32, height: 32,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              backgroundColor: '#2563eb', color: '#fff',
              borderRadius: '8px', fontSize: '0.85rem', fontWeight: 600,
            }}>
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
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                fontSize: '0.85rem',
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

export default ManagerEmployees;