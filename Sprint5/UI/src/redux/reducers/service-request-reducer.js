import { ADD_SERVICE_REQUEST, DELETE_SERVICE_REQUEST, GET_ALL_SERVICE_REQUESTS, SET_FILTER, SET_PAGE } from "../actions/service-request-action"


const initialState={
    page:0,
    totalPages:1,
    size:5,
    filter:'ALL',
    requests:[]
}

const serviceRequestReducer = (state=initialState,action) => {
   
    switch(action.type){
        case DELETE_SERVICE_REQUEST:
            const temp=[...state.requests].filter(request=>request.id!=action.payload)
            return {
                 ...state,
                 requests:[...temp]
            }

        case ADD_SERVICE_REQUEST:
            return {
                ...state,
                requests:[...state.requests,action.payload]
            }
        case GET_ALL_SERVICE_REQUESTS:
            return {
                ...state,
                requests:[...action.payload.list],
                totalPages:action.payload.totalPages
            }

        case SET_PAGE:
            return {
                ...state,
                page:action.payload
            }

        case SET_FILTER:
            return {
                ...state,
                filter:action.payload
            }
            
        default:
            return state
    }

}

export default serviceRequestReducer
