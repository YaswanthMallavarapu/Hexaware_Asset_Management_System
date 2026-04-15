import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BsPlus, BsX, BsChevronLeft, BsChevronRight, BsLaptop, BsSearch, BsFunnel } from 'react-icons/bs';

const AdminAssets = () => {

  const [assets, setAssets] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [size] = useState(6);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formError, setFormError] = useState('');

  // form fields
  const [assetNo, setAssetNo] = useState('');
  const [assetName, setAssetName] = useState('');
  const [assetModel, setAssetModel] = useState('');
  const [assetCategoryId, setAssetCategoryId] = useState('');
  const [manufacturedDate, setManufacturedDate] = useState('');
  const [price, setPrice] = useState('');

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

  // Handler 1 — fetch ALL assets
  const fetchAllAssets = async (currentPage) => {
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8082/api/asset/get-all?page=${currentPage}&size=${size}`,
        config
      );
      setAssets(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  // Handler 2 — fetch assets by STATUS
  const fetchAssetsByStatus = async (currentPage, status) => {
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8082/api/asset/get/status/${status}?page=${currentPage}&size=${size}`,
        config
      );
      setAssets(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  // Handler 3 — add asset
  const handleAddAsset = async (e) => {
    e.preventDefault();
    setLoading(true);
    setFormError('');
    try {
      await axios.post(
        'http://localhost:8082/api/asset/add',
        {
          assetNo,
          assetName,
          assetModel,
          assetCategoryId: Number(assetCategoryId),
          manufacturedDate: manufacturedDate || null,
          price: price ? Number(price) : null,
        },
        config
      );
      setAssetNo('');
      setAssetName('');
      setAssetModel('');
      setAssetCategoryId('');
      setManufacturedDate('');
      setPrice('');
      setShowModal(false);
      setPage(0);
      if (filter === 'ALL') fetchAllAssets(0);
      else fetchAssetsByStatus(0, filter);
    } catch (err) {
      if (err.response?.status === 401) {
        setFormError('Unauthorized. Please log in again.');
      } else if (err.response?.status === 403) {
        setFormError('Access denied. You cannot add assets.');
      } else if (err.response?.status === 400) {
        setFormError('Invalid input. Please check the fields.');
      } else {
        setFormError('Failed to add asset. Please try again.');
      }
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (filter === 'ALL') {
      fetchAllAssets(page);
    } else {
      fetchAssetsByStatus(page, filter);
    }
  }, [page, filter]);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setPage(0);
  };

  const handleNext = () => { if (hasMore) setPage(prev => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage(prev => prev - 1); };

  const filterButtons = [
    { label: 'All', value: 'ALL' },
    { label: 'Available', value: 'AVAILABLE' },
    { label: 'Allocated', value: 'ALLOCATED' },
    { label: 'In Service', value: 'IN_SERVICE' },
    { label: 'Retired', value: 'RETIRED' },
  ];

  const statusStyles = {
    AVAILABLE:   { bg: "#f0fdf4", color: "#16a34a", border: "#bbf7d0", label: "Available" },
    ALLOCATED:   { bg: "#eff6ff", color: "#2563eb", border: "#bfdbfe", label: "Allocated" },
    IN_SERVICE:  { bg: "#fffbeb", color: "#d97706", border: "#fde68a", label: "In Service" },
    RETIRED:     { bg: "#fef2f2", color: "#dc2626", border: "#fecaca", label: "Retired" },
  };

  const cardColors = [
    { bg: "#eff6ff", icon: "#2563eb", border: "#bfdbfe" },
    { bg: "#f0fdf4", icon: "#16a34a", border: "#bbf7d0" },
    { bg: "#fffbeb", icon: "#d97706", border: "#fde68a" },
    { bg: "#f5f3ff", icon: "#7c3aed", border: "#ddd6fe" },
    { bg: "#fef2f2", icon: "#dc2626", border: "#fecaca" },
    { bg: "#f0f9ff", icon: "#0284c7", border: "#bae6fd" },
  ];

  const filteredAssets = assets.filter(asset =>
    asset.assetName.toLowerCase().includes(search.toLowerCase()) ||
    asset.assetNo.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Assets</h5>
          <p className="text-muted small mb-0">Manage all assets</p>
        </div>
        <button
          className="btn d-flex align-items-center gap-2"
          style={{
            backgroundColor: "#2563eb", color: "#fff",
            borderRadius: "10px", padding: "8px 18px",
            fontWeight: 500, border: "none",
          }}
          onClick={() => setShowModal(true)}
        >
          <BsPlus size={20} /> Add Asset
        </button>
      </div>

      {/* Search + Filter Row */}
      <div className="d-flex flex-wrap align-items-center gap-3 mb-4">
        <div className="position-relative" style={{ maxWidth: 280, flex: 1 }}>
          <BsSearch size={15} style={{ position: "absolute", left: 12, top: "50%", transform: "translateY(-50%)", color: "#94a3b8" }} />
          <input
            type="text"
            className="form-control"
            placeholder="Search by name or asset no..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ paddingLeft: 36, borderRadius: "10px", border: "1.5px solid #e2e8f0", fontSize: "0.875rem", backgroundColor: "#fff" }}
          />
        </div>
        <div className="d-flex align-items-center gap-2 flex-wrap">
          <BsFunnel size={16} className="text-muted" />
          {filterButtons.map(btn => (
            <button
              key={btn.value}
              onClick={() => handleFilterChange(btn.value)}
              style={{
                border: filter === btn.value ? "none" : "1px solid #e2e8f0",
                borderRadius: "8px", padding: "6px 14px",
                fontSize: "0.83rem", fontWeight: 500, cursor: "pointer",
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
          <button onClick={() => setError('')} style={{ background: "none", border: "none", cursor: "pointer", fontSize: "1rem", color: "#dc2626" }}>✕</button>
        </div>
      )}

      {/* Cards Grid */}
      {filteredAssets.length === 0 ? (
        <div className="text-center text-muted py-5">
          <BsLaptop size={40} className="mb-3 opacity-25" />
          <p>No assets found</p>
        </div>
      ) : (
        <div className="row g-3">
          {filteredAssets.map((asset, index) => {
            const color = cardColors[index % cardColors.length];
            const status = statusStyles[asset.assetStatus] || statusStyles.AVAILABLE;
            return (
              <div className="col-12 col-sm-6 col-lg-4" key={asset.id}>
                <div
                  className="card border-0 h-100"
                  style={{
                    borderRadius: "16px",
                    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
                    transition: "transform 0.2s ease, box-shadow 0.2s ease",
                    border: `1px solid ${color.border}`,
                  }}
                  onMouseEnter={e => {
                    e.currentTarget.style.transform = "translateY(-3px)";
                    e.currentTarget.style.boxShadow = "0 8px 24px rgba(0,0,0,0.10)";
                  }}
                  onMouseLeave={e => {
                    e.currentTarget.style.transform = "translateY(0)";
                    e.currentTarget.style.boxShadow = "0 2px 12px rgba(0,0,0,0.06)";
                  }}
                >
                  <div className="card-body p-4">

                    {/* Icon + Status row */}
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div style={{
                        width: 48, height: 48, borderRadius: "12px",
                        backgroundColor: color.bg, border: `1.5px solid ${color.border}`,
                        display: "flex", alignItems: "center", justifyContent: "center",
                      }}>
                        <BsLaptop size={22} color={color.icon} />
                      </div>
                      <span style={{
                        fontSize: "0.75rem", fontWeight: 600,
                        backgroundColor: status.bg, color: status.color,
                        border: `1px solid ${status.border}`,
                        borderRadius: "6px", padding: "3px 10px",
                      }}>
                        {status.label}
                      </span>
                    </div>

                    {/* Asset Name */}
                    <h6 className="fw-bold mb-0" style={{ color: "#1e293b", fontSize: "1rem" }}>
                      {asset.assetName}
                    </h6>
                    <p className="text-muted mb-2" style={{ fontSize: "0.8rem" }}>{asset.assetModel}</p>

                    {/* Divider */}
                    <div style={{ borderTop: `1px solid ${color.border}`, marginBottom: 12 }} />

                    {/* Details */}
                    <div className="d-flex flex-column gap-1">
                      <div className="d-flex justify-content-between">
                        <span style={{ fontSize: "0.78rem", color: "#94a3b8", fontWeight: 500 }}>Asset No</span>
                        <span style={{ fontSize: "0.78rem", color: "#1e293b", fontWeight: 600 }}>{asset.assetNo}</span>
                      </div>
                      <div className="d-flex justify-content-between">
                        <span style={{ fontSize: "0.78rem", color: "#94a3b8", fontWeight: 500 }}>Category ID</span>
                        <span style={{ fontSize: "0.78rem", color: "#1e293b", fontWeight: 600 }}>#{asset.categoryId}</span>
                      </div>
                      <div className="d-flex justify-content-between">
                        <span style={{ fontSize: "0.78rem", color: "#94a3b8", fontWeight: 500 }}>Manufactured</span>
                        <span style={{ fontSize: "0.78rem", color: "#1e293b", fontWeight: 600 }}>
                          {asset.manufacturedDate ? asset.manufacturedDate : "—"}
                        </span>
                      </div>
                    </div>

                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Pagination */}
      <div className="d-flex justify-content-between align-items-center mt-4 px-1">
        <span className="text-muted" style={{ fontSize: "0.85rem" }}>Page {page + 1}</span>
        <div className="d-flex align-items-center gap-2">
          <button
            onClick={handlePrev}
            disabled={page === 0}
            style={{
              border: "1px solid #e2e8f0", borderRadius: "8px", padding: "6px 12px",
              backgroundColor: page === 0 ? "#f8fafc" : "#fff",
              color: page === 0 ? "#cbd5e1" : "#475569",
              cursor: page === 0 ? "not-allowed" : "pointer",
              display: "flex", alignItems: "center", gap: "4px",
              fontSize: "0.85rem", fontWeight: 500,
            }}
          >
            <BsChevronLeft size={14} /> Prev
          </button>

          <span style={{
            width: 32, height: 32, display: "flex", alignItems: "center", justifyContent: "center",
            backgroundColor: "#2563eb", color: "#fff",
            borderRadius: "8px", fontSize: "0.85rem", fontWeight: 600,
          }}>
            {page + 1}
          </span>

          <button
            onClick={handleNext}
            disabled={!hasMore}
            style={{
              border: "1px solid #e2e8f0", borderRadius: "8px", padding: "6px 12px",
              backgroundColor: !hasMore ? "#f8fafc" : "#fff",
              color: !hasMore ? "#cbd5e1" : "#475569",
              cursor: !hasMore ? "not-allowed" : "pointer",
              display: "flex", alignItems: "center", gap: "4px",
              fontSize: "0.85rem", fontWeight: 500,
            }}
          >
            Next <BsChevronRight size={14} />
          </button>
        </div>
      </div>

      {/* Add Asset Modal */}
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
              width: "100%", maxWidth: "480px", boxShadow: "0 20px 60px rgba(0,0,0,0.15)",
              maxHeight: "90vh", overflowY: "auto",
            }}
            onClick={e => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div className="d-flex justify-content-between align-items-center mb-4">
              <div>
                <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Add New Asset</h5>
                <p className="text-muted small mb-0">Fill in the asset details below</p>
              </div>
              <button
                onClick={() => setShowModal(false)}
                style={{ background: "#f1f5f9", border: "none", borderRadius: "8px", padding: "6px 10px", cursor: "pointer" }}
              >
                <BsX size={20} color="#64748b" />
              </button>
            </div>

            {formError && (
              <div className="alert alert-danger py-2 px-3 mb-3" style={{ borderRadius: "10px", fontSize: "0.85rem" }}>
                {formError}
              </div>
            )}

            <form onSubmit={handleAddAsset}>
              <div className="row g-3">
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Asset No</label>
                  <input
                    type="text" className="form-control" placeholder="e.g. AST-001"
                    value={assetNo} onChange={e => setAssetNo(e.target.value)} required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Asset Name</label>
                  <input
                    type="text" className="form-control" placeholder="e.g. Dell Laptop"
                    value={assetName} onChange={e => setAssetName(e.target.value)} required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Model</label>
                  <input
                    type="text" className="form-control" placeholder="e.g. XPS 15"
                    value={assetModel} onChange={e => setAssetModel(e.target.value)} required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Category ID</label>
                  <input
                    type="number" className="form-control" placeholder="e.g. 1"
                    value={assetCategoryId} onChange={e => setAssetCategoryId(e.target.value)} required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Manufactured Date</label>
                  <input
                    type="date" className="form-control"
                    value={manufacturedDate} onChange={e => setManufacturedDate(e.target.value)}
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>Price</label>
                  <input
                    type="number" className="form-control" placeholder="e.g. 75000"
                    value={price} onChange={e => setPrice(e.target.value)} step="0.01"
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
              </div>

              <div className="d-flex gap-2 mt-4">
                <button
                  type="button" className="btn w-50"
                  onClick={() => setShowModal(false)}
                  style={{ borderRadius: "10px", border: "1.5px solid #e2e8f0", color: "#64748b", fontWeight: 500 }}
                >
                  Cancel
                </button>
                <button
                  type="submit" className="btn w-50" disabled={loading}
                  style={{ borderRadius: "10px", backgroundColor: "#2563eb", color: "#fff", fontWeight: 500, border: "none" }}
                >
                  {loading ? "Adding..." : "Add Asset"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default AdminAssets;