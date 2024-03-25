import { Module } from '@nestjs/common';
import { CveService } from './cve.service';
import { CveController } from './cve.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AffectedProduct, Cvss, Description, Exploit, Fix, PatchCommit, RawDescription, VdoCharacteristic, Vulnerability, VulnerabilityVersion } from 'src/entities';

@Module({
    imports: [
        TypeOrmModule.forFeature([
            Vulnerability,
            VulnerabilityVersion,
            Description,
            Exploit,
            RawDescription,
            AffectedProduct,
            VdoCharacteristic,
            Cvss,
            PatchCommit,
            Fix
        ]),
    ],
    controllers: [CveController],
    providers: [CveService],
})
export class CveModule {}
