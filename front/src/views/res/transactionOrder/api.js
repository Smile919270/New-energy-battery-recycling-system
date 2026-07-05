import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getTransactionOrderOne = (params) => {
    return getRequest('/transactionOrder/getOne', params)
}
export const getTransactionOrderList = (params) => {
    return getRequest('/transactionOrder/getByPage', params)
}
export const getTransactionOrderCount = (params) => {
    return getRequest('/transactionOrder/count', params)
}
export const addTransactionOrder = (params) => {
    return postRequest('/transactionOrder/insert', params)
}
export const editTransactionOrder = (params) => {
    return postRequest('/transactionOrder/update', params)
}
export const addOrEditTransactionOrder = (params) => {
    return postRequest('/transactionOrder/insertOrUpdate', params)
}
export const deleteTransactionOrder = (params) => {
    return postRequest('/transactionOrder/delByIds', params)
}
export const getNotSellAllSales = (params) => {
    return getRequest('/salesOrder/getNotSellAll', params)
}
export const getNotSellAllPurchase = (params) => {
    return getRequest('/purchaseOrder/getNotSellAll', params)
}
export const addOrder = (params) => {
    return postRequest('/transactionOrder/addOrder', params)
}