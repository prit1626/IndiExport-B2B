import axiosClient from './axiosClient';

/**
 * Generic file upload — uses the backend's /api/v1/upload endpoint.
 * Files are stored on the local server disk (or Cloudinary if the backend is configured for it).
 *
 * @param {File} file - Browser File object
 * @param {Function} onProgress - optional (percent) => void callback
 * @returns {Promise<string>} the public URL of the uploaded file
 */
export const uploadFile = async (file, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);

    const { data } = await axiosClient.post('/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: onProgress
            ? (e) => onProgress(Math.round((e.loaded * 100) / e.total))
            : undefined,
    });

    return data.url;
};

const uploadApi = { uploadFile };
export default uploadApi;
