import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getSalesOrderOne = (params) => {
    return getRequest('/salesOrder/getOne', params)
}
export const getSalesOrderList = (params) => {
    return getRequest('/salesOrder/getByPage', params)
}
export const getSalesOrderCount = (params) => {
    return getRequest('/salesOrder/count', params)
}
export const addSalesOrder = (params) => {
    return postRequest('/salesOrder/insert', params)
}
export const editSalesOrder = (params) => {
    return postRequest('/salesOrder/update', params)
}
export const addOrEditSalesOrder = (params) => {
    return postRequest('/salesOrder/insertOrUpdate', params)
}
export const deleteSalesOrder = (params) => {
    return postRequest('/salesOrder/delByIds', params)
}
export const getResourceList = (params) => {
    return getRequest('/resource/getAll', params)
}
export const sellSalesOrder = (params) => {
    return postRequest('/salesOrder/sell', params)
}