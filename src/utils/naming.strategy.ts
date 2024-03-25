import { DefaultNamingStrategy, NamingStrategyInterface, Table } from 'typeorm';

export class SnakeNamingStrategy extends DefaultNamingStrategy implements NamingStrategyInterface
{
    tableName(className: string, customName: string): string {
        return customName ? customName : className.toLowerCase();
    }

    columnName(
        propertyName: string,
        customName: string,
        embeddedPrefixes: string[],
    ): string {
        return customName
            ? customName
            : `${this.snakeCase(embeddedPrefixes.join('_'))}${this.snakeCase(propertyName)}`;
    }

    relationName(propertyName: string): string {
        return this.snakeCase(propertyName);
    }

    private snakeCase(str: string): string {
        return str.replace(/([A-Z])/g, (match) => `_${match.toLowerCase()}`);
    }
}
