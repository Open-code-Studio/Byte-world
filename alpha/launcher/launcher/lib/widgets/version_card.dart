
import 'package:flutter/material.dart';

import '../providers/launcher_provider.dart';

class VersionCard extends StatelessWidget {
  final GameVersion version;
  final bool isSelected;
  final VoidCallback onSelect;

  const VersionCard({
    super.key,
    required this.version,
    required this.isSelected,
    required this.onSelect,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Card(
      elevation: isSelected ? 4 : 2,
      color: isSelected 
          ? theme.colorScheme.primaryContainer 
          : theme.colorScheme.surface,
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: InkWell(
        onTap: onSelect,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: _getTypeColor(version.type),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  _getTypeIcon(version.type),
                  color: Colors.white,
                  size: 24,
                ),
              ),
              
              const SizedBox(width: 16),
              
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      version.name,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Released: ${version.releaseDate} | Size: ${version.size}',
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ],
                ),
              ),
              
              if (isSelected)
                Icon(
                  Icons.check_circle,
                  color: theme.colorScheme.primary,
                  size: 24,
                ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getTypeColor(String type) {
    switch (type.toLowerCase()) {
      case 'release':
        return const Color(0xFF4CAF50);
      case 'beta':
        return const Color(0xFFFF9800);
      case 'alpha':
        return const Color(0xFF9C27B0);
      default:
        return const Color(0xFF607D8B);
    }
  }

  IconData _getTypeIcon(String type) {
    switch (type.toLowerCase()) {
      case 'release':
        return Icons.check_circle;
      case 'beta':
        return Icons.beta;
      case 'alpha':
        return Icons.flash_on;
      default:
        return Icons.package;
    }
  }
}
