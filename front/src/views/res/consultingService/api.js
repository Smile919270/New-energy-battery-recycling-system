import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getConsultingServiceOne = (params) => {
    return getRequest('/consultingService/getOne', params)
}
export const getConsultingServiceList = (params) => {
    return getRequest('/consultingService/getByPage', params)
}
export const getConsultingServiceCount = (params) => {
    return getRequest('/consultingService/count', params)
}
export const addConsultingService = (params) => {
    return postRequest('/consultingService/insert', params)
}
export const editConsultingService = (params) => {
    return postRequest('/consultingService/update', params)
}
export const addOrEditConsultingService = (params) => {
    return postRequest('/consultingService/insertOrUpdate', params)
}
export const deleteConsultingService = (params) => {
    return postRequest('/consultingService/delByIds', params)
}
export const replyConsultingService = (params) => {
    return postRequest('/consultingService/reply', params)
}
export const getReceptionistList = (params) => {
    return getRequest('/receptionist/getAll', params)
}