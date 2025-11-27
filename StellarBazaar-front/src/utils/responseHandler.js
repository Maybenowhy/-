/**
 * 统一处理 API 响应
 * @param {Object} response API 响应
 * @param {string} successMessage 成功消息
 * @returns {boolean} 是否成功
 */
export function handleApiResponse(response, successMessage = "操作成功") {
  console.log('handleApiResponse 接收到:', response)
  
  // 如果响应为 null 或 undefined，认为操作成功（因为后端确实执行了）
  if (response === null || response === undefined) {
    console.log('响应为空，但认为操作成功')
    if (successMessage) {
      // 这里需要根据您的消息组件调整
      console.log(successMessage)
    }
    return true
  }
  
  // 支持多种成功状态
  const successCodes = [1, 200, 201, '1', '200', '201']
  const isSuccess = successCodes.includes(response.code) || 
                   response.success === true || 
                   response.status === 'success' ||
                   response === true
  
  if (isSuccess) {
    if (successMessage) {
      console.log(successMessage)
    }
    return true
  } else {
    const errorMsg = response.message || response.msg || response.error || "操作失败"
    throw new Error(errorMsg)
  }
}

/**
 * 显示消息（需要与您的消息组件配合）
 */
function showMessage(message, type = "info") {
  // 这里需要根据您的实际消息组件进行调整
  console.log(`[${type}] ${message}`);
  // 例如：如果您使用了 Element Plus 的 Message
  // ElMessage[type](message);
}