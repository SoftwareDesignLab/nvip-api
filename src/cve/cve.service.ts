import { Injectable, Patch } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import {
    AffectedProduct,
    Cvss,
    Exploit,
    Fix,
    PatchCommit,
    RawDescription,
    VdoCharacteristic,
    Vulnerability,
    VulnerabilityVersion,
} from 'src/entities';
import { Repository } from 'typeorm';

@Injectable()
export class CveService {
    constructor(
        @InjectRepository(Vulnerability)
        private vulnerabilityRepository: Repository<Vulnerability>,
        @InjectRepository(VulnerabilityVersion)
        private vulnerabilityVersionRepository: Repository<VulnerabilityVersion>,

        @InjectRepository(Exploit)
        private exploitRepository: Repository<Exploit>,

        @InjectRepository(RawDescription)
        private rawDescriptionRepository: Repository<RawDescription>,

        @InjectRepository(AffectedProduct)
        private affectedProductRepository: Repository<AffectedProduct>,

        @InjectRepository(VdoCharacteristic)
        private vdoRepository: Repository<VdoCharacteristic>,

        @InjectRepository(Cvss)
        private cvssRepository: Repository<Cvss>,

        @InjectRepository(Fix)
        private fixRepository: Repository<Fix>,

        @InjectRepository(PatchCommit)
        private patchRepository: Repository<PatchCommit>,
    ) {}

    async getCveDetails(cveId:string){
        const cveDetails = await this.vulnerabilityRepository.findOne({
            where: {
                cveId: cveId,
            },
            relations:[
                'nvdData',
                'mitreData',
                'nvdData.sourceUrl'
            ]
        });
        return cveDetails;
    }
    async getCveDescription(cveId: string) {
        const cve = await this.vulnerabilityRepository.findOne({
            where: {
                cveId: cveId,
            },
        });
        var version = await this.vulnerabilityVersionRepository.findOne({
            where: { vulnerabilityVersionId: cve.vulnVersionId },
            relations: ['description'],
        });
        return version.description;
    }

    async getCveExploits(cveId: string) {
        const exploits = await this.exploitRepository.find({
            where: {
                cveId: cveId,
            },
        });
        return exploits
    }

    async getCveRawDescriptions(cveId: string) {
        const descriptions = await this.rawDescriptionRepository.find({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
            order: {
                createdDate: 'ASC',
            },
        });
        return descriptions;
    }

    async getCveAffectedProducts(cveId: string) {
        const products = await this.affectedProductRepository.find({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
            order: {
                affectedProductId: 'DESC',
            },
        });
        return products;
    }

    async getCpe(cveId: string) {
        const products = await this.affectedProductRepository.find({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
            order: {
                affectedProductId: 'DESC',
            },
        });
        const cpes = products.map(p => p.cpe);
        return  cpes;
    }

    async getVdoLabels(cveId: string) {
        const cve = await this.vulnerabilityRepository.findOne({
            where: {
                cveId: cveId,
            },
        });
        var version = await this.vulnerabilityVersionRepository.findOne({
            where: { vulnerabilityVersionId: cve.vulnVersionId },
            relations: ['vdoSet.vdoCharacteristics'],
        });

        var cvss = await this.cvssRepository.findOne({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
        });

        return {
            vdoLabels: version.vdoSet.vdoCharacteristics,
            cvss: cvss?cvss.baseScore:null,
        };
    }

    async getFixes(cveId:string){
        const fixes = await this.fixRepository.find({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
        });
        return fixes;
    }

    async getPatches(cveId:string){
        const patches = await this.patchRepository.find({
            where: {
                vulnerability: {
                    cveId: cveId,
                },
            },
            relations:[
                    'sourceUrl'
                ]
        });
        return patches;
    }
}
