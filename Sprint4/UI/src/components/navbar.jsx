import React from "react";

const Navbar = () => {

  const handleSearch = (e) => {
    e.preventDefault();
    console.log("Search clicked");
  };

  const handleLogout = () => {
    console.log("Logout clicked");
  };

  return (
    <nav className="navbar navbar-light bg-light">
      <div className="container-fluid">
        
        <span className="navbar-brand mb-0 h1"></span>

        <form className="d-flex" onSubmit={handleSearch}>
          <input
            className="form-control me-2"
            type="search"
            placeholder="Search"
          />

          <button className="btn btn-primary me-2" type="submit">
            Search
          </button>

          <button
            className="btn btn-success"
            type="button"
            onClick={handleLogout}
          >
            Logout
          </button>
        </form>

      </div>
    </nav>
  );
};

export default Navbar;