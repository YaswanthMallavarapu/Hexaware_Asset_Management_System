import React from "react";

function AdminDashboard() {
  return (
    <div className="container-fluid">
      <div className="row p-1">

        {/* Content */}
        <div className="col-lg-8 mt-2 p-4">

          {/* Row 1 */}
          <div className="row">
            <div className="col-md-6 mb-3">
              <div className="card text-center p-3 shadow-sm">
                <div>
                  <p className="mb-1">Manager Icon</p>
                </div>
                <div>
                  <h3>12</h3>
                </div>
                <div>
                  <p className="text-muted">Total Managers</p>
                </div>
              </div>
            </div>

            <div className="col-md-6 mb-3">
              <div className="card text-center p-3 shadow-sm">
                <div>
                  <p className="mb-1">Asset Category Icon</p>
                </div>
                <div>
                  <h3>12</h3>
                </div>
                <div>
                  <p className="text-muted">Total Asset Categories</p>
                </div>
              </div>
            </div>
          </div>

          {/* Row 2 */}
          <div className="row mt-3">
            <div className="col-md-6 mb-3">
              <div className="card text-center p-3 shadow-sm">
                <div>
                  <p className="mb-1">Assets</p>
                </div>
                <div>
                  <h3>1263</h3>
                </div>
                <div>
                  <p className="text-muted">Total Assets</p>
                </div>
              </div>
            </div>

            <div className="col-md-6 mb-3">
              <div className="card text-center p-3 shadow-sm">
                <div>
                  <p className="mb-1">Admins</p>
                </div>
                <div>
                  <h3>4</h3>
                </div>
                <div>
                  <p className="text-muted">Total Admins</p>
                </div>
              </div>
            </div>
          </div>

        </div>

      </div>
    </div>
  );
}

export default AdminDashboard;