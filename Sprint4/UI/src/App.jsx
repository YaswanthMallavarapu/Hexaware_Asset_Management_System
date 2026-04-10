import Managers from "./components/admin-managers"
import AdminDashboard from "./components/adminDashboard"
import Assets from "./components/asset"
import AssetCategories from "./components/assetCategories"
import Navbar from "./components/navbar"
import Sidebar from "./components/sidebar"



function App() {


  return (
    <>
    <Navbar/>
     <Sidebar/>
     <Managers/>
     <AdminDashboard/>
     <AssetCategories/>
     <Assets/>
    </>
  )
}

export default App
