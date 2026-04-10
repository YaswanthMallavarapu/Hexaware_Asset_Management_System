import React from "react";

function AssetCategories() {
  return (
    <div className="container mt-4">

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h4 className="mb-0">Asset Categories</h4>
          <small className="text-muted">Manage your asset categories</small>
        </div>
        <button className="btn btn-primary">Add Category</button>
      </div>

      {/* Row 1 */}
      <div className="row">
        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Laptops</strong><br />
            <small className="text-muted">Electronic Devices</small>
            <p className="mb-1 mt-2">250 Assets</p>
            <span className="text-success">Active</span>
          </div>
        </div>

        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Printers</strong><br />
            <small className="text-muted">Office Equipment</small>
            <p className="mb-1 mt-2">45 Assets</p>
            <span className="text-danger">In Service</span>
          </div>
        </div>
      </div>

      {/* Row 2 */}
      <div className="row">
        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Furniture</strong><br />
            <small className="text-muted">Office Infrastructure</small>
            <p className="mb-1 mt-2">180 Assets</p>
            <span className="text-warning">Retired</span>
          </div>
        </div>

        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Mobile Phones</strong><br />
            <small className="text-muted">Communication Devices</small>
            <p className="mb-1 mt-2">95 Assets</p>
            <span className="text-success">Active</span>
          </div>
        </div>
      </div>

    </div>
  );
}

export default AssetCategories;