export const formatDate = (dateString) => {
    if (!dateString) return '-';

    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('en-IN', options);
};

export const formatShortDate = (dateString) => {
    if (!dateString) return '-';

    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-IN', options);
};

export const formatTime = (dateString) => {
    if (!dateString) return '-';

    const options = { hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleTimeString('en-IN', options);
}
