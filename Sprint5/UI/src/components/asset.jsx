import React from "react";

function Assets() {
  return (
    <div className="container mt-4">

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h4 className="mb-0">Assets</h4>
          <small className="text-muted">Manage all company assets</small>
        </div>
        <button className="btn btn-primary">Add Asset</button>
      </div>

      {/* Row 1 */}
      <div className="row">
        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Dell Latitude 5420</strong><br />
            <small className="text-muted">Category: Laptops</small>
            <p className="mb-1 mt-2">Assigned to: Arjun Reddy</p>
            <span className="text-success">Allocated</span>
          </div>
        </div>

        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>HP LaserJet Pro</strong><br />
            <small className="text-muted">Category: Printers</small>
            <p className="mb-1 mt-2">Location: Admin Office</p>
            <span className="text-success">Available</span>
          </div>
        </div>
      </div>

      {/* Row 2 */}
      <div className="row">
        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>iPhone 14</strong><br />
            <small className="text-muted">Category: Mobile Phones</small>
            <p className="mb-1 mt-2">Assigned to: Priya Sharma</p>
            <span className="text-warning">In Service</span>
          </div>
        </div>

        <div className="col-md-6 mb-3">
          <div className="border rounded p-3 bg-white">
            <strong>Office Chair - Model X</strong><br />
            <small className="text-muted">Category: Furniture</small>
            <p className="mb-1 mt-2">Location: Conference Room</p>
            <span className="text-success">Available</span>
          </div>
        </div>
      </div>

    </div>
  );
}

export default Assets;