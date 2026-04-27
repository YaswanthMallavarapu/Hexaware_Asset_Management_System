import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  cancelReturnRequest,
  getAllAllocations,
  returnAssetRequest,
  setFilter,
  setPage,
} from "../../redux/actions/asset-allocation-action";
import { addServiceRequest } from "../../redux/actions/service-request-action";
import { BsChevronLeft, BsChevronRight, BsFunnel } from "react-icons/bs";
import "../../styles/employee-allocations.css";

const EmployeeAssetAllocations = () => {
  const dispatch = useDispatch();

  const { allocations, page, totalPages, size, filter } = useSelector(
    (state) => state.assetAllocationReducer
  );

  const [selectedAllocation, setSelectedAllocation] = useState(null);
  const [remarks, setRemarks] = useState("");
  const [showModal, setShowModal] = useState(false);

  const filters = [
    { name: "All", value: "ALL" },
    { name: "Allocated", value: "ALLOCATED" },
    { name: "Return Requested", value: "RETURN_REQUESTED" },
    { name: "Service Requested", value: "SERVICE_REQUESTED" },
    { name: "Returned", value: "RETURNED" },
  ];

  useEffect(() => {
    dispatch(getAllAllocations(page, size, filter));
  }, [dispatch, page, filter]);

  const hasPrev = () => page > 0;
  const hasNext = () => page < totalPages - 1;

  const formatDate = (date) => {
    if (!date) return "—";
    return new Date(date).toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  return (
    <div className="container-fluid py-4 px-4 employee-allocations-wrapper">
      
      {/* Header */}
      <div className="mb-4">
        <h5 className="fw-bold mb-0">My Allocated Assets</h5>
        <p className="text-muted small mb-0">
          Manage returns and service requests
        </p>
      </div>

      {/* Filters */}
      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <BsFunnel size={14} className="text-muted" />
        {filters.map((f) => (
          <button
            key={f.value}
            onClick={() => {
              dispatch(setFilter(f.value));
              dispatch(setPage(0));
            }}
            className={`filter-btn ${
              filter === f.value ? "active-filter" : ""
            }`}
          >
            {f.name}
          </button>
        ))}
      </div>

      {/* Card */}
      <div className="card allocation-card">
        <div className="card-body p-0">
          <table className="table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Asset</th>
                <th>Manager</th>
                <th>Allocated Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {allocations.length === 0 ? (
                <tr>
                  <td colSpan="6" className="text-center py-5 text-muted">
                    No allocations found
                  </td>
                </tr>
              ) : (
                allocations.map((allocation, index) => (
                  <tr key={allocation.id}>
                    <td>{page * size + index + 1}</td>
                    <td className="fw-semibold">{allocation.assetName}</td>
                    <td>{allocation.managerName}</td>
                    <td>{formatDate(allocation.allocatedDate)}</td>
                    <td>
                      <span
                        className={`status-badge status-${allocation.status?.toLowerCase()}`}
                      >
                        {allocation.status}
                      </span>
                    </td>
                    <td className="d-flex gap-2">
                      {allocation.status === "ALLOCATED" && (
                        <>
                          <button
                            className="btn btn-outline-warning btn-sm action-btn"
                            onClick={() =>
                              dispatch(returnAssetRequest(allocation))
                            }
                          >
                            Return
                          </button>

                          <button
                            className="btn btn-outline-primary btn-sm action-btn"
                            onClick={() => {
                              setSelectedAllocation(allocation);
                              setRemarks("");
                              setShowModal(true);
                            }}
                          >
                            Service
                          </button>
                        </>
                      )}

                      {allocation.status === "RETURN_REQUESTED" && (
                        <button
                          className="btn btn-outline-danger btn-sm action-btn"
                          onClick={() =>
                            dispatch(cancelReturnRequest(allocation))
                          }
                        >
                          Cancel
                        </button>
                      )}

                      {(allocation.status === "SERVICE_REQUESTED" ||
                        allocation.status === "RETURNED") && (
                        <span className="text-muted">—</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="d-flex justify-content-between align-items-center px-4 py-3 border-top">
          <span className="text-muted small">
            Page {page + 1} of {totalPages}
          </span>

          <div className="d-flex gap-2">
            <button
              className="pagination-btn"
              disabled={!hasPrev()}
              onClick={() => hasPrev() && dispatch(setPage(page - 1))}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <button
              className="pagination-btn"
              disabled={!hasNext()}
              onClick={() => hasNext() && dispatch(setPage(page + 1))}
            >
              Next <BsChevronRight size={14} />
            </button>
          </div>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal fade show d-block custom-modal">
          <div className="modal-dialog">
            <div className="modal-content">

              <div className="modal-header">
                <h5 className="modal-title">Request Service</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setShowModal(false)}
                ></button>
              </div>

              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  dispatch(
                    addServiceRequest(remarks, selectedAllocation.id)
                  );
                  setShowModal(false);
                  setSelectedAllocation(null);
                  setRemarks("");
                }}
              >
                <div className="modal-body">

                  <div className="mb-3">
                    <label className="form-label">Asset</label>
                    <input
                      type="text"
                      className="form-control"
                      value={selectedAllocation?.assetName}
                      readOnly
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Remarks</label>
                    <textarea
                      className="form-control"
                      value={remarks}
                      onChange={(e) => setRemarks(e.target.value)}
                      required
                    />
                  </div>

                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-light"
                    onClick={() => setShowModal(false)}
                  >
                    Cancel
                  </button>

                  <button type="submit" className="btn btn-primary">
                    Submit Request
                  </button>
                </div>
              </form>

            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default EmployeeAssetAllocations;