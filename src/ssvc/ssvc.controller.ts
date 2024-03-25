import {
    Controller,
    Get,
    Param,
} from '@nestjs/common';
import { SsvcService } from './ssvc.service';

@Controller('ssvc')
export class SsvcController {
    constructor(private readonly ssvcService: SsvcService) {}

    @Get(':id')
    async findOne(@Param('id') cveId: string) {
        return await this.ssvcService.findOne(cveId);
    }
}
