import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BsPlus, BsX, BsChevronLeft, BsChevronRight, BsLaptop, BsFunnel } from 'react-icons/bs';
import '../../styles/admin-assets.css';

const AdminAssets = () => {

  const [assets, setAssets] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [size] = useState(6);
  const [totalPages, setTotalPages] = useState(1)
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [categories, setCategories] = useState([])

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
  };

  const fetchAllAssets = async (currentPage) => {
    try {
      const res = await axios.get(
        `http://localhost:8082/api/asset/get-all?page=${currentPage}&size=${size}`,
        config
      );
      setAssets(res.data.list);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      handleError(err);
    }
  };

  const fetchAssetsByStatus = async (currentPage, status) => {
    try {
      const res = await axios.get(
        `http://localhost:8082/api/asset/get/status/${status}?page=${currentPage}&size=${size}`,
        config
      );
      setAssets(res.data.list);
      setTotalPages(res.data.totalPages);

    } catch (err) {
      handleError(err);
    }
  };

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
      setFormError('Failed to add asset.');
    } finally {
      setLoading(false);
    }
  };

  const getAllCategories = async () => {
    const apiGetAll = 'http://localhost:8082/api/asset-category/get-all-categories'
    const response = await axios.get(apiGetAll, config)
    console.log(response);
    setCategories(response.data);
  }

  useEffect(() => {
    if (filter === 'ALL') fetchAllAssets(page);
    else fetchAssetsByStatus(page, filter);
    getAllCategories()

  }, [page, filter]);

  const handleNext = () => { if (page < totalPages - 1) setPage(prev => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage(prev => prev - 1); };

  const filterButtons = [
    { label: 'All', value: 'ALL' },
    { label: 'Available', value: 'AVAILABLE' },
    { label: 'Allocated', value: 'ALLOCATED' },
    { label: 'In Service', value: 'IN_SERVICE' },
    { label: 'Retired', value: 'RETIRED' },
  ];

  const statusStyles = {
    AVAILABLE: { bg: "#f0fdf4", color: "#16a34a", border: "#bbf7d0", label: "Available" },
    ALLOCATED: { bg: "#eff6ff", color: "#2563eb", border: "#bfdbfe", label: "Allocated" },
    IN_SERVICE: { bg: "#fffbeb", color: "#d97706", border: "#fde68a", label: "In Service" },
    RETIRED: { bg: "#fef2f2", color: "#dc2626", border: "#fecaca", label: "Retired" },
  };

  const cardColors = [
    { bg: "#eff6ff", icon: "#2563eb", border: "#bfdbfe" },
    { bg: "#f0fdf4", icon: "#16a34a", border: "#bbf7d0" },
    { bg: "#fffbeb", icon: "#d97706", border: "#fde68a" },
    { bg: "#f5f3ff", icon: "#7c3aed", border: "#ddd6fe" },
    { bg: "#fef2f2", icon: "#dc2626", border: "#fecaca" },
    { bg: "#f0f9ff", icon: "#0284c7", border: "#bae6fd" },
  ];

  return (
    <div className="container-fluid py-4 px-4 admin-bg">

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0 admin-title">Assets</h5>
          <p className="text-muted small mb-0">Manage all assets</p>
        </div>

        <button className="btn d-flex align-items-center gap-2 add-btn" onClick={() => setShowModal(true)}>
          <BsPlus size={20} /> Add Asset
        </button>
      </div>

      {/* Filters */}
      <div className="d-flex flex-wrap align-items-center gap-3 mb-4">
        <div className="d-flex align-items-center gap-2 flex-wrap">
          <BsFunnel size={16} className="text-muted" />
          {filterButtons.map(btn => (
            <button
              key={btn.value}
              onClick={() => { setFilter(btn.value); setPage(0); }}
              className={`filter-btn ${filter === btn.value ? 'active' : ''}`}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>

      {/* Cards */}
      {assets.length === 0 ? (
        <div className="text-center text-muted py-5">
          <BsLaptop size={40} className="mb-3 opacity-25" />
          <p>No assets found</p>
        </div>
      ) : (
        <div className="row g-3">
          {assets.map((asset, index) => {
            const color = cardColors[index % cardColors.length];
            const status = statusStyles[asset.assetStatus] || statusStyles.AVAILABLE;

            return (
              <div className="col-12 col-sm-6 col-lg-4" key={asset.id}>
                <div className="card border-0 h-100 asset-card">

                  <div className="card-body p-4">

                    <div className="d-flex justify-content-between mb-3">
                      <div className="icon-box" style={{ background: color.bg, borderColor: color.border }}>
                        <BsLaptop size={22} color={color.icon} />
                      </div>

                      <span
                        className="status-badge"
                        style={{

                          color: status.color,
                          borderColor: status.border
                        }}
                      >
                        {status.label}
                      </span>
                    </div>

                    <h6 className="fw-bold mb-0 admin-title">{asset.assetName}</h6>
                    <p className="text-muted mb-2 small">{asset.assetModel}</p>

                    <div className="divider" style={{ borderColor: color.border }} />

                    <div className="d-flex flex-column gap-1 small">
                      <div className="d-flex justify-content-between">
                        <span className="label">Asset No</span>
                        <span className="value">{asset.assetNo}</span>
                      </div>
                      <div className="d-flex justify-content-between">
                        <span className="label">Category ID</span>
                        <span className="value">#{asset.categoryId}</span>
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
        <span className="text-muted small">Page {page + 1}</span>

        <div className="d-flex gap-2">
          <button onClick={handlePrev} disabled={page === 0} className="page-btn">
            <BsChevronLeft /> Prev
          </button>

          <span className="page-number">{page + 1}</span>

          <button onClick={handleNext} disabled={!(page < (totalPages - 1))} className="page-btn">
            Next <BsChevronRight />
          </button>
        </div>
      </div>

      {/* Add Asset Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">

            {/* Header */}
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h6 className="fw-bold mb-0">Add Asset</h6>
              <button className="btn" onClick={() => setShowModal(false)}>
                <BsX size={20} />
              </button>
            </div>

            {/* Form */}
            <form onSubmit={handleAddAsset}>

              <div className="mb-2">
                <label className="form-label small">Asset No</label>
                <input
                  type="text"
                  className="form-control"
                  value={assetNo}
                  onChange={(e) => setAssetNo(e.target.value)}
                  required
                />
              </div>

              <div className="mb-2">
                <label className="form-label small">Asset Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={assetName}
                  onChange={(e) => setAssetName(e.target.value)}
                  required
                />
              </div>

              <div className="mb-2">
                <label className="form-label small">Model</label>
                <input
                  type="text"
                  className="form-control"
                  value={assetModel}
                  onChange={(e) => setAssetModel(e.target.value)}
                  required
                />
              </div>

              <div className="mb-2">
                <label className="form-label small">Category </label>
                {/* <input
            type="number"
            className="form-control"
            value={assetCategoryId}
            onChange={(e) => setAssetCategoryId(e.target.value)}
            required
          /> */}
                <select name="" id="" className="form-control"
                  value={assetCategoryId}
                  onChange={(e) => setAssetCategoryId(e.target.value)}
                  required>
                    <option value="" >Select Category</option>
                  {
                    categories.map((c, index) => (
                      <option value={c.id} key={c.id}>{c.name}</option>
                    ))
                  }
                </select>
              </div>

              <div className="mb-2">
                <label className="form-label small">Manufactured Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={manufacturedDate}
                  onChange={(e) => setManufacturedDate(e.target.value)}
                />
              </div>

              <div className="mb-3">
                <label className="form-label small">Price</label>
                <input
                  type="number"
                  className="form-control"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                />
              </div>

              {formError && <p className="text-danger small">{formError}</p>}

              <button type="submit" className="btn add-btn w-100" disabled={loading}>
                {loading ? "Adding..." : "Add Asset"}
              </button>

            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default AdminAssets;