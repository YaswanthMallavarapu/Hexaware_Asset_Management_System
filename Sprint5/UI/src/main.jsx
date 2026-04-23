import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Login from './components/auth/login.jsx'
import AdminDashboard from './components/admin/admin-dashboard.jsx'
import ManagerDashboard from './components/manager/manager-dashboard.jsx'
import EmployeeDashboard from './components/employee/employee-dashboard.jsx'
import Sidebar from './components/admin/admin-sidebar.jsx'
import AdminAdmins from './components/admin/admin-admins.jsx'
import AdminLayout from './components/admin/admin-layout.jsx'
import AdminManagers from './components/admin/admin-mangers.jsx'
import AdminCategories from './components/admin/admin-categories.jsx'
import AdminAssets from './components/admin/admin-assets.jsx'
import ManagerLayout from './components/manager/manager-layout.jsx'
import ManagerEmployee from './components/manager/manager-employee.jsx'
import ManagerCategories from './components/manager/manager-categories.jsx'
import ManagerAssets from './components/manager/manager-assets.jsx'
import ManagerAssetRequest from './components/manager/manager-asset-request.jsx'
import ManagerAssetAllocations from './components/manager/manager-asset-allocations.jsx'
import ManagerServiceRequest from './components/manager/manager-service-request.jsx'
import ManagerAudit from './components/manager/manager-audit.jsx'
import EmployeeLayout from './components/employee/employee-layout.jsx'
import EmployeeAssets from './components/employee/employee-assets.jsx'
import EmployeeAssetAllocations from './components/employee/employee-asset-allocations.jsx'
import EmployeeAssetRequest from './components/employee/employee-asset-request.jsx'
import EmployeeServiceRequest from './components/employee/employee-service-request.jsx'
import ManagerProfile from './components/manager/manager-profile.jsx'
import AuditResults from './components/manager/manager-audit-results.jsx'
import EmployeeProfile from './components/employee/employee-profile.jsx'
import { Provider } from 'react-redux'
import { store } from './store.js'


const routes=createBrowserRouter([
  {

    path:"",
    element:<App/>
  },
  {
    path:"/login",
    element:<Login/>
  },
  {
    path:"/admin",
    element:<AdminLayout/>,
    children:[
      {
        index:true,
        element:<AdminDashboard/>
      },
      {
        path:"admins",
        element:<AdminAdmins/>
      },
      {
        path:"managers",
        element:<AdminManagers/>
      },
      {
        path:"categories",
        element:<AdminCategories/>
      },
      {
        path:"assets",
        element:<AdminAssets/>
      },
      {
        path:"dashboard",
        element:<AdminAssets/>
      },


    ]
  },
  {
    path:"/manager",
    element:<ManagerLayout/>,
    children:[

      {
        index:true,
        element:<ManagerDashboard/>
      },
      {
        path:"employees",
        element:<ManagerEmployee/>
      },
      {
        path:"categories",
        element:<ManagerCategories/>
      },
      {
        path:"assets",
        element:<ManagerAssets/>
      },
      {
        path:"asset-request",
        element:<ManagerAssetRequest/>
      },{
        path:"asset-allocations",
        element:<ManagerAssetAllocations/>
      },{
        path:"service-request",
        element:<ManagerServiceRequest/>
      },{
        path:"audit",
        element:<ManagerAudit/>
      },
      {
        path:"dashboard",
        element:<ManagerDashboard/>
      },
      {
        path:"profile",
        element:<ManagerProfile/>
      },
      {
        path:"audit-results/:auditId",
        element:<AuditResults/>
      },


    ]
  }
  ,{
    path:"/employee",
    element:<EmployeeLayout/>,
    children:[
      {
        index:true,
        element:<EmployeeDashboard/>
      },
      {
        path:"assets",
        element:<EmployeeAssets/>
      },
      {
        path:"asset-allocation",
        element:<EmployeeAssetAllocations/>
      },
      {
        path:"asset-request",
        element:<EmployeeAssetRequest/>
      },
      {
        path:"service-request",
        element:<EmployeeServiceRequest/>
      },
      {
        path:"profile",
        element:<EmployeeProfile/>
      },
      {
        path:"dashboard",
        element:<EmployeeDashboard/>
      },



    ]
  }
  

])

createRoot(document.getElementById('root')).render(
  <Provider store={store}>
  <RouterProvider router={routes}>
    <App />
    </RouterProvider>
    </Provider>
  
)
