import { applyMiddleware, combineReducers, createStore } from "redux";
import AssetAllocationReducer from "./redux/reducers/asset-allocation-reducer";
import { thunk } from "redux-thunk";
import serviceRequestReducer from "./redux/reducers/service-request-reducer";

const reducers=combineReducers({
    assetAllocationReducer:AssetAllocationReducer,
    serviceRequestReducer:serviceRequestReducer
})
export const store=createStore(reducers,applyMiddleware(thunk))