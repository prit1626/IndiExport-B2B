import { startOfDay, endOfDay, subDays, startOfMonth, formatISO } from 'date-fns';

export const getDateRangePresets = () => [
    { label: 'Last 7 Days', value: '7d' },
    { label: 'Last 30 Days', value: '30d' },
    { label: 'Last 90 Days', value: '90d' },
    { label: 'This Month', value: 'month' },
    { label: 'Custom', value: 'custom' }
];

export const calculateDateRange = (preset) => {
    const now = new Date();
    let from, to = endOfDay(now);

    switch (preset) {
        case '7d':
            from = subDays(now, 7);
            break;
        case '30d':
            from = subDays(now, 30);
            break;
        case '90d':
            from = subDays(now, 90);
            break;
        case 'month':
            from = startOfMonth(now);
            break;
        default:
            from = subDays(now, 30); // Default fallback
    }

    return {
        from: startOfDay(from).toISOString(),
        to: to.toISOString()
    };
};
