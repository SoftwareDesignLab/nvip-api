import {
    Controller,
    Get,
    Post,
    Body,
    Patch,
    Param,
    Delete,
    Query,
} from '@nestjs/common';
import { CveService } from './cve.service';

@Controller('cve')
export class CveController {
    constructor(private readonly cveService: CveService) {}

    @Get('details/:id')
    async getDetails(@Param('id') cveId: string) {
        return await this.cveService.getCveDetails(cveId);
    }

    @Get('description/:id')
    getDescription(@Param('id') cveId: string) {
        return this.cveService.getCveDescription(cveId);
    }

    
    @Get('exploits/:id')
    getExploits(@Param('id') cveId: string) {
        return this.cveService.getCveExploits(cveId);
    }

    @Get('rawdescriptions/:id')
    getRawDescriptions(@Param('id') cveId: string) {
        return this.cveService.getCveRawDescriptions(cveId);
    }

    @Get('cpe/:id')
    getCpe(@Param('id') cveId: string) {
        return this.cveService.getCpe(cveId);
    }

    @Get('affectedproducts/:id')
    getAffectedProducts(@Param('id') cveId: string) {
        return this.cveService.getCveAffectedProducts(cveId);
    }

    @Get('vdolabels/:id')
    async getVdoLabels(@Param('id') cveId: string) {
        return await this.cveService.getVdoLabels(cveId);
    }
    @Get('fixes/:id')
    async getFixes(@Param('id') cveId: string) {
        return await this.cveService.getFixes(cveId);
    }

    @Get('patches/:id')
    async getPatches(@Param('id') cveId: string) {
        return await this.cveService.getPatches(cveId);
    }
}
