import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getPurchaseOrderOne = (params) => {
    return getRequest('/purchaseOrder/getOne', params)
}
export const getPurchaseOrderList = (params) => {
    return getRequest('/purchaseOrder/getByPage', params)
}
export const getPurchaseOrderCount = (params) => {
    return getRequest('/purchaseOrder/count', params)
}
export const addPurchaseOrder = (params) => {
    return postRequest('/purchaseOrder/insert', params)
}
export const editPurchaseOrder = (params) => {
    return postRequest('/purchaseOrder/update', params)
}
export const addOrEditPurchaseOrder = (params) => {
    return postRequest('/purchaseOrder/insertOrUpdate', params)
}
export const deletePurchaseOrder = (params) => {
    return postRequest('/purchaseOrder/delByIds', params)
}
export const getResourceList = (params) => {
    return getRequest('/resource/getAll', params)
}
export const sellPurchaseOrder = (params) => {
    return postRequest('/purchaseOrder/sell', params)
}