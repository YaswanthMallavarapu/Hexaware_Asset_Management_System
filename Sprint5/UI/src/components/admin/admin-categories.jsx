import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BsPlus, BsX, BsChevronLeft, BsChevronRight, BsGrid, BsSearch } from 'react-icons/bs';

const AdminCategories = () => {

  const [categories, setCategories] = useState([]);
  const [categoryName, setCategoryName] = useState('');
  const [text, setText] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(6);
  const [hasMore, setHasMore] = useState(true);
  const [search, setSearch] = useState('');

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

  // Handler 1 — fetch all categories
  const fetchAllCategories = async (currentPage) => {
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8082/api/asset-category/get-all?page=${currentPage}&size=${size}`,
        config
      );
      setCategories(res.data);
      setHasMore(res.data.length === size);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    fetchAllCategories(page);
  }, [page]);

  // Handler 2 — add category
  const handleAddCategory = async (e) => {
    e.preventDefault();
    setLoading(true);
    setFormError('');
    try {
      await axios.post(
        'http://localhost:8082/api/asset-category/add',
        { categoryName, text },
        config
      );
      setCategoryName('');
      setText('');
      setShowModal(false);
      setPage(0);
      fetchAllCategories(0);
    } catch (err) {
      if (err.response?.status === 401) {
        setFormError('Unauthorized. Please log in again.');
      } else if (err.response?.status === 403) {
        setFormError('Access denied. You cannot add categories.');
      } else if (err.response?.status === 400) {
        setFormError('Invalid input. Please check the fields.');
      } else {
        setFormError('Failed to add category. Please try again.');
      }
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleNext = () => { if (hasMore) setPage(prev => prev + 1); };
  const handlePrev = () => { if (page > 0) setPage(prev => prev - 1); };

  const filteredCategories = categories.filter(cat =>
    cat.categoryName.toLowerCase().includes(search.toLowerCase())
  );

  // color palette for cards
  const cardColors = [
    { bg: "#eff6ff", icon: "#2563eb", border: "#bfdbfe" },
    { bg: "#f0fdf4", icon: "#16a34a", border: "#bbf7d0" },
    { bg: "#fffbeb", icon: "#d97706", border: "#fde68a" },
    { bg: "#f5f3ff", icon: "#7c3aed", border: "#ddd6fe" },
    { bg: "#fef2f2", icon: "#dc2626", border: "#fecaca" },
    { bg: "#f0f9ff", icon: "#0284c7", border: "#bae6fd" },
  ];

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Categories</h5>
          <p className="text-muted small mb-0">Manage all asset categories</p>
        </div>
        <button
          className="btn d-flex align-items-center gap-2"
          style={{
            backgroundColor: "#2563eb",
            color: "#fff",
            borderRadius: "10px",
            padding: "8px 18px",
            fontWeight: 500,
            border: "none",
          }}
          onClick={() => setShowModal(true)}
        >
          <BsPlus size={20} />
          Add Category
        </button>
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

      {/* Cards Grid */}
      {filteredCategories.length === 0 ? (
        <div className="text-center text-muted py-5">
          <BsGrid size={40} className="mb-3 opacity-25" />
          <p>No categories found</p>
        </div>
      ) : (
        <div className="row g-3">
          {filteredCategories.map((cat, index) => {
            const color = cardColors[index % cardColors.length];
            return (
              <div className="col-12 col-sm-6 col-lg-4" key={cat.id}>
                <div
                  className="card border-0 h-100"
                  style={{
                    borderRadius: "16px",
                    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
                    transition: "transform 0.2s ease, box-shadow 0.2s ease",
                    cursor: "default",
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

                    {/* Icon + ID row */}
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div
                        style={{
                          width: 48, height: 48,
                          borderRadius: "12px",
                          backgroundColor: color.bg,
                          border: `1.5px solid ${color.border}`,
                          display: "flex", alignItems: "center", justifyContent: "center",
                        }}
                      >
                        <BsGrid size={22} color={color.icon} />
                      </div>
                      <span
                        style={{
                          fontSize: "0.75rem",
                          fontWeight: 600,
                          backgroundColor: color.bg,
                          color: color.icon,
                          border: `1px solid ${color.border}`,
                          borderRadius: "6px",
                          padding: "2px 8px",
                        }}
                      >
                        #{cat.id}
                      </span>
                    </div>

                    {/* Category Name */}
                    <h6 className="fw-bold mb-1" style={{ color: "#1e293b", fontSize: "1rem" }}>
                      {cat.categoryName}
                    </h6>

                    {/* Description */}
                    <p
                      className="text-muted mb-3"
                      style={{
                        fontSize: "0.82rem",
                        lineHeight: 1.5,
                        display: "-webkit-box",
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: "vertical",
                        overflow: "hidden",
                      }}
                    >
                      {cat.description || "No description provided."}
                    </p>

                    {/* Divider */}
                    <div style={{ borderTop: `1px solid ${color.border}`, marginBottom: 12 }} />

                    {/* Stats Row */}
                    <div className="d-flex justify-content-between">
                      <div className="text-center">
                        <div style={{ fontSize: "1.1rem", fontWeight: 700, color: "#1e293b" }}>{cat.quantity}</div>
                        <div style={{ fontSize: "0.72rem", color: "#94a3b8", fontWeight: 500 }}>Total</div>
                      </div>
                      <div style={{ width: 1, backgroundColor: color.border }} />
                      <div className="text-center">
                        <div style={{ fontSize: "1.1rem", fontWeight: 700, color: "#16a34a" }}>{cat.remaining}</div>
                        <div style={{ fontSize: "0.72rem", color: "#94a3b8", fontWeight: 500 }}>Remaining</div>
                      </div>
                      <div style={{ width: 1, backgroundColor: color.border }} />
                      <div className="text-center">
                        <div style={{ fontSize: "1.1rem", fontWeight: 700, color: "#dc2626" }}>{cat.quantity - cat.remaining}</div>
                        <div style={{ fontSize: "0.72rem", color: "#94a3b8", fontWeight: 500 }}>In Use</div>
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
              border: "1px solid #e2e8f0",
              borderRadius: "8px",
              padding: "6px 12px",
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
              display: "flex", alignItems: "center", gap: "4px",
              fontSize: "0.85rem", fontWeight: 500,
            }}
          >
            Next <BsChevronRight size={14} />
          </button>
        </div>
      </div>

      {/* Add Category Modal */}
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
              width: "100%", maxWidth: "440px", boxShadow: "0 20px 60px rgba(0,0,0,0.15)",
            }}
            onClick={e => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div className="d-flex justify-content-between align-items-center mb-4">
              <div>
                <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>Add New Category</h5>
                <p className="text-muted small mb-0">Fill in the details below</p>
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

            <form onSubmit={handleAddCategory}>
              <div className="row g-3">
                <div className="col-12">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>
                    Category Name
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Laptops"
                    value={categoryName}
                    onChange={e => setCategoryName(e.target.value)}
                    required
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0" }}
                  />
                </div>
                <div className="col-12">
                  <label className="form-label fw-semibold" style={{ fontSize: "0.82rem", color: "#475569" }}>
                    Description
                  </label>
                  <textarea
                    className="form-control"
                    placeholder="Brief description of this category..."
                    value={text}
                    onChange={e => setText(e.target.value)}
                    required
                    rows={3}
                    style={{ borderRadius: "10px", fontSize: "0.9rem", border: "1.5px solid #e2e8f0", resize: "none" }}
                  />
                </div>
              </div>

              <div className="d-flex gap-2 mt-4">
                <button
                  type="button"
                  className="btn w-50"
                  onClick={() => setShowModal(false)}
                  style={{ borderRadius: "10px", border: "1.5px solid #e2e8f0", color: "#64748b", fontWeight: 500 }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn w-50"
                  disabled={loading}
                  style={{ borderRadius: "10px", backgroundColor: "#2563eb", color: "#fff", fontWeight: 500, border: "none" }}
                >
                  {loading ? "Adding..." : "Add Category"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default AdminCategories;