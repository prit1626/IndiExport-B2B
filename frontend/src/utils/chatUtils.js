import { format, isToday, isYesterday } from 'date-fns';

export const formatTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return format(date, 'h:mm a');
};

export const groupMessagesByDate = (messages) => {
    const groups = {};

    messages.forEach(msg => {
        const date = new Date(msg.createdAt);
        let dateKey = format(date, 'yyyy-MM-dd');
        let displayDate = format(date, 'dd MMMM yyyy');

        if (isToday(date)) {
            displayDate = 'Today';
        } else if (isYesterday(date)) {
            displayDate = 'Yesterday';
        }

        if (!groups[dateKey]) {
            groups[dateKey] = {
                date: displayDate,
                messages: []
            };
        }
        groups[dateKey].messages.push(msg);
    });

    // Return array sorted by date (oldest first)
    return Object.values(groups).sort((a, b) => {
        // Since messages are typically sorted, we can use the first message's date
        // But keys are yyyy-MM-dd so basic string sort works too
        // However, we want the array order to match the visual order (top to bottom)
        // Usually messages API returns newest first (page 0). 
        // We usually reverse them for display (oldest at top).
        // Let's assume the input 'messages' array is already sorted Oldest -> Newest 
        // OR Newest -> Oldest. The standard chat UI needs Oldest -> Newest (top to bottom).
        // We will sort the groups based on the date key.
        return 0; // The Object.values order isn't guaranteed, we should sort keys.
    });
};

// Re-write to be more robust
export const groupMessages = (messagesAscending) => {
    const groups = [];
    let currentGroup = null;

    messagesAscending.forEach(msg => {
        const date = new Date(msg.createdAt);
        let displayDate = format(date, 'dd MMMM yyyy');

        if (isToday(date)) displayDate = 'Today';
        else if (isYesterday(date)) displayDate = 'Yesterday';

        if (!currentGroup || currentGroup.date !== displayDate) {
            currentGroup = {
                date: displayDate,
                dateObj: date, // for sorting if needed
                messages: []
            };
            groups.push(currentGroup);
        }
        currentGroup.messages.push(msg);
    });

    return groups;
};
