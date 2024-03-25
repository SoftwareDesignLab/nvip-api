import { Module } from '@nestjs/common';

import { AppController } from './app.controller';
import { AppService } from './app.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import {
    AffectedProduct,
    CpeSet,
    Cvss,
    Description,
    Exploit,
    Fix,
    PatchCommit,
    PatchSourceUrl,
    RawDescription,
    Role,
    RunHistory,
    SSVC,
    Timegap,
    User,
    VdoCharacteristic,
    VdoSet,
    Vulnerability,
    VulnerabilityVersion,
    NvdData,
    MitreData,
    NvdSourceUrl,
} from './entities';

import { SnakeNamingStrategy } from './utils/naming.strategy';
import { VulnerabilityModule } from './vulnerability/vulnerability.module';
import { CveModule } from './cve/cve.module';
import { SsvcModule } from './ssvc/ssvc.module';
import { ConfigModule } from '@nestjs/config';
@Module({
    imports: [
        ConfigModule.forRoot({
            envFilePath: '.env',
            isGlobal: true,
        }),
        TypeOrmModule.forRoot({
            type: 'mysql',
            host: process.env.DATABASE_HOST,
            port:+process.env.DATABASE_PORT || 3306,
            username: process.env.DATABASE_USER,
            password: process.env.DATABASE_PASSWORD,
            database: process.env.DATABASE_NAME,
            entities: [
                AffectedProduct,
                CpeSet,
                Cvss,
                Description,
                Exploit,
                Fix,
                PatchCommit,
                PatchSourceUrl,
                RawDescription,
                Role,
                RunHistory,
                SSVC,
                Timegap,
                User,
                VdoCharacteristic,
                VdoSet,
                Vulnerability,
                VulnerabilityVersion,
                NvdData,
                MitreData,
                NvdSourceUrl,
            ],
            synchronize: false,
            namingStrategy: new SnakeNamingStrategy(),
            logging: false,
        }),
        VulnerabilityModule,
        CveModule,
        SsvcModule,
    ],
    controllers: [AppController],
    providers: [AppService],
})
export class AppModule {}
